package com.example.easykhata.ui.fragments.sales
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.sales.Sale
import com.example.easykhata.databinding.FragmentReciptBinding
import com.example.easykhata.ui.adapters.ReceiptItemsAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.sales.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ReciptFragment : Fragment(R.layout.fragment_recipt) {
    lateinit var binding: FragmentReciptBinding
    private val salesViewModel: SalesViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val args: ReciptFragmentArgs by navArgs()
    private lateinit var receiptAdapter: ReceiptItemsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentReciptBinding.bind(view)
        setupClicks()
        loadReceipt()
        setupRecyclerView()
        observeSaleDetails()
    }
    private fun setupClicks() {
        binding.doneButton.setOnClickListener {
            salesViewModel.resetSaleDetailsState()
            findNavController().popBackStack(R.id.salesFragment, false)
        }
        binding.reciptToolbar.setNavigationOnClickListener {
            salesViewModel.resetSaleDetailsState()
            findNavController().popBackStack(R.id.salesFragment, false)
        }
    }
    private fun setupRecyclerView() {
        receiptAdapter = ReceiptItemsAdapter()
        binding.invoiceRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ReciptFragment.receiptAdapter
        }
    }
    private fun observeSaleDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                salesViewModel.saleDetailsState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            setUpRecipt(state.data)
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        UiState.Idle -> {
                            hideProgress()
                        }
                    }
                }
            }
        }
    }
    private fun setUpRecipt(sale: Sale) {
        binding.apply {
            reciptToolbar.subtitle= getString(R.string.inv, sale.invoiceNumber)
            invoiceNumber.text = getString(R.string.inv, sale.invoiceNumber)
            saleType.text = sale.paymentMethod.name
            dateInvoice.text = formatReceiptDate(sale.date)
            val hasCustomer = sale.customerName.isNotBlank()
            textView34.visibility = if (hasCustomer) View.VISIBLE else View.GONE
            customerName.visibility = if (hasCustomer) View.VISIBLE else View.GONE
            if (hasCustomer) {
                customerName.text = sale.customerName
            }
            jamaRaqam.text =getString(R.string.rs_10 , sale.totalPrice + sale.discount)
            totalAmount.text = getString(R.string.rs_10, sale.totalPrice)
            val hasDiscount = sale.discount > 0
            textView39.visibility = if (hasDiscount) View.VISIBLE else View.GONE
            saleDiscount.visibility = if (hasDiscount) View.VISIBLE else View.GONE
            if (hasDiscount) {
                saleDiscount.text = getString(R.string.rs_10,sale.discount)
            }
            val hasNote = sale.note.isNotBlank()
            textView42.visibility = if (hasNote) View.VISIBLE else View.GONE
            noteRecipt.visibility = if (hasNote) View.VISIBLE else View.GONE
            noteLine.visibility= if(hasNote) View.VISIBLE else View.GONE
            if (hasNote) {
                noteRecipt.text = sale.note
            }
            receiptAdapter.submitList(sale.items)
        }
    }
    private fun loadReceipt(){
        val businessId= businessViewModel.businessId.value
        if(businessId.isNullOrBlank()){
            return
        }
        salesViewModel.getSale(businessId,args.saleId)
    }
    private fun formatReceiptDate(timestamp: Long): String {
        val outputFormat = java.text.SimpleDateFormat(
            "dd MMM yyyy · hh:mm a",
            java.util.Locale.ENGLISH
        )
        return outputFormat.format(java.util.Date(timestamp))
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}