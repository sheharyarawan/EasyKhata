package com.example.easykhata.ui.fragments.customers
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.databinding.FragmentSearchCustomerBinding
import com.example.easykhata.ui.adapters.SearchCustomerAdapter
import com.example.easykhata.ui.viewModels.customer.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class SearchCustomerFragment : Fragment(R.layout.fragment_search_customer) {
    lateinit var binding: FragmentSearchCustomerBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var searchCustomerAdapter: SearchCustomerAdapter
    private var allCustomers: List<CustomerWithBalance> = emptyList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentSearchCustomerBinding.bind(view)
        setupRecyclerView()
        observeCustomers()
        setupSearch()
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            addCustomerClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    private fun setupRecyclerView() {
        searchCustomerAdapter = SearchCustomerAdapter { customerId ->
            val action= SearchCustomerFragmentDirections.actionSearchCustomerFragmentToCustomerDetailFragment(
                customerId
            )
            findNavController().navigate(action)
        }
        binding.searchCustomerRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchCustomerAdapter
        }
    }
    private fun observeCustomers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.allCustomersState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            allCustomers = state.data
                            searchCustomerAdapter.submitList(allCustomers)
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun setupSearch() {
        binding.addCustomerEditText.doAfterTextChanged { editable ->
            val query = editable.toString().trim()
            val filteredCustomers = if (query.isEmpty()) {
                allCustomers
            } else {
                allCustomers.filter { customer ->
                    customer.customer.name.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }
            searchCustomerAdapter.submitList(filteredCustomers)
        }
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}