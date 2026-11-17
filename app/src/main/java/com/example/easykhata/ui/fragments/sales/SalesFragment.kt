package com.example.easykhata.ui.fragments.sales
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.sales.Sale
import com.example.easykhata.databinding.FragmentSalesBinding
import com.example.easykhata.ui.adapters.SalesAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.sales.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
@AndroidEntryPoint
class SalesFragment : Fragment(R.layout.fragment_sales) {
    private lateinit var binding: FragmentSalesBinding
    private val salesViewModel: SalesViewModel by viewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private lateinit var salesAdapter: SalesAdapter
    private var allSales = emptyList<Sale>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSalesBinding.bind(view)
        setUpAdapter()
        loadSales()
        setUpClicks()
        setUpSearch()
        observeSales()
    }
    private fun setUpAdapter() {
        salesAdapter = SalesAdapter(
            onSaleClick = { sale ->
                val action= SalesFragmentDirections.actionSalesFragmentToReciptFragment(
                    sale.id
                )
                findNavController().navigate(action)
            },
            onSaleMenuClick = { sale, anchor ->
                // Sale menu actions will be added later.
            }
        )
        binding.salesRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = salesAdapter
        }
    }
    private fun observeSales() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                salesViewModel.salesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showLoading()
                        }
                        is UiState.Success -> {
                            hideLoading()
                            salesAdapter.submitList(state.data)
                            binding.ordersTv.text = state.data.size.toString()
                        }
                        is UiState.Error -> {
                            hideLoading()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UiState.Idle -> {
                            hideLoading()
                        }
                    }
                }
            }
        }
    }
    private fun setUpClicks() {
        binding.apply {
            itemsManageCard.setOnClickListener {
                findNavController().navigate(
                    R.id.action_salesFragment_to_itemsManageFragment
                )
            }
            saleDarjKareinButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_salesFragment_to_saleDarjkareinFragment
                )
            }
        }
    }
    private fun loadSales(){
        val businessId = businessViewModel.businessId.value.orEmpty()
        if (businessId.isNotBlank()) {
            salesViewModel.observeSales(businessId)
        } else {
            Toast.makeText(
                requireContext(),
                "Please select a business first",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun setUpSearch() {
        binding.searchEt.doAfterTextChanged { editable ->
            filterSales(editable?.toString().orEmpty())
        }
    }
    private fun filterSales(query: String) {
        val searchQuery = query.trim().lowercase(Locale.ROOT)
        val filteredSales = allSales
            .sortedByDescending { it.date }
            .filter { sale ->
                val customerName = sale.customerName.lowercase(Locale.ROOT)
                val invoiceNumber = sale.invoiceNumber.lowercase(Locale.ROOT)
                val amount = sale.totalPrice.toString()
                searchQuery.isBlank() ||
                        customerName.contains(searchQuery) ||
                        invoiceNumber.contains(searchQuery) ||
                        amount.contains(searchQuery)
            }
        salesAdapter.submitList(filteredSales)
    }
    private fun showLoading() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideLoading() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}