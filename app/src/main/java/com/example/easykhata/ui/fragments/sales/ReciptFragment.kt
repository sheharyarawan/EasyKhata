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
import com.example.easykhata.utils.showConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ReciptFragment : Fragment(R.layout.fragment_recipt) {
    lateinit var binding: FragmentReciptBinding
    private val salesViewModel: SalesViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val args: ReciptFragmentArgs by navArgs()
    private lateinit var receiptAdapter: ReceiptItemsAdapter
    private var currentSale: Sale? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentReciptBinding.bind(view)
        setupClicks()
        loadReceipt()
        setupRecyclerView()
        observeSaleDetails()
        observeDeleteSale()
    }
    private fun setupClicks() {
        binding.apply {
            doneButton.setOnClickListener {
                salesViewModel.resetSaleDetailsState()
                findNavController().popBackStack(R.id.salesFragment, false)
            }
            reciptToolbar.setNavigationOnClickListener {
                salesViewModel.resetSaleDetailsState()
                findNavController().popBackStack(R.id.salesFragment, false)
            }
            reciptToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.deleteExpense -> {
                        currentSale?.let { sale ->
                            showDeleteConfirmation(sale)
                        }
                        true
                    }
                    else -> false
                }
            }
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
                            setUpReceipt(state.data)
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
    private fun observeDeleteSale() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                salesViewModel.deleteSaleState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showProgress()
                        is UiState.Success -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                "Sale deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            salesViewModel.resetDeleteSaleState()
                            salesViewModel.resetSaleDetailsState()
                            findNavController().popBackStack(
                                R.id.salesFragment,
                                false
                            )
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            salesViewModel.resetDeleteSaleState()
                        }
                        UiState.Idle -> Unit
                    }
                }
            }
        }
    }
    private fun setUpReceipt(sale: Sale) = with(binding) {
        currentSale = sale
        val invoice = getString(R.string.inv, sale.invoiceNumber)
        reciptToolbar.subtitle = invoice
        invoiceNumber.text = invoice
        saleType.text = sale.paymentMethod.name
        dateInvoice.text = formatReceiptDate(sale.date)
        val hasCustomer = sale.customerName.isNotBlank()
        textView34.setVisibleOrGone(hasCustomer)
        customerName.setVisibleOrGone(hasCustomer)
        if (hasCustomer) {
            customerName.text = sale.customerName
        }
        jamaRaqam.text = getString(
            R.string.rs_11,
            sale.totalPrice + sale.discount
        )

        totalAmount.text = getString(
            R.string.rs_11,
            sale.totalPrice
        )
        val hasDiscount = sale.discount > 0
        textView39.setVisibleOrGone(hasDiscount)
        saleDiscount.setVisibleOrGone(hasDiscount)
        if (hasDiscount) {
            saleDiscount.text = getString(
                R.string.rs_11,
                sale.discount
            )
        }
        val hasNote = sale.note.isNotBlank()
        textView42.setVisibleOrGone(hasNote)
        noteRecipt.setVisibleOrGone(hasNote)
        noteLine.setVisibleOrGone(hasNote)
        if (hasNote) {
            noteRecipt.text = sale.note
        }
        receiptAdapter.submitList(sale.items)
    }
    private fun loadReceipt(){
        val businessId= businessViewModel.businessId.value
        if(businessId.isNullOrBlank()){
            return
        }
        salesViewModel.getSale(businessId,args.saleId)
    }
    private fun showDeleteConfirmation(sale: Sale) {
        showConfirmationDialog(
            title = "Sale delete karein?",
            message = "Kya aap sale delete karna chahte hein?",
            positiveButtonText = "DELETE",
            negativeButtonText = "CANCEL"
        ) {
            val businessId = businessViewModel.businessId.value.orEmpty()
            salesViewModel.deleteSale(businessId, sale)
        }
    }
    private fun formatReceiptDate(timestamp: Long): String {
        val outputFormat = java.text.SimpleDateFormat(
            "dd MMM yyyy · hh:mm a",
            java.util.Locale.ENGLISH
        )
        return outputFormat.format(java.util.Date(timestamp))
    }
    fun View.setVisibleOrGone(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}