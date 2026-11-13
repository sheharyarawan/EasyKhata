package com.example.easykhata.ui.fragments.account
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.FragmentWasooliCustomerBinding
import com.example.easykhata.ui.adapters.CustomerSelectAdapter
import com.example.easykhata.ui.fragments.customers.bottomSheets.WasooliBottomSheet
import com.example.easykhata.ui.viewModels.account.WasooliViewModel
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class WasooliCustomerFragment :
    Fragment(R.layout.fragment_wasooli_customer) {
    lateinit var binding: FragmentWasooliCustomerBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val wasooliViewModel: WasooliViewModel by activityViewModels()
    private lateinit var customerAdapter: CustomerSelectAdapter
    private var businessId = ""
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWasooliCustomerBinding.bind(view)
        setupToolbar()
        setupRecyclerView()
        setupSearch()
        observeData()
        loadData()
    }
    private fun setupToolbar() {
        binding.customerSelectToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setupRecyclerView() {
        customerAdapter = CustomerSelectAdapter { customerWithBalance ->
            WasooliBottomSheet.newInstance(
                customerId = customerWithBalance.customer.id,
                customerName = customerWithBalance.customer.name,
                businessId = businessId
            ).show(parentFragmentManager, "WasooliBottomSheet")
        }
        binding.customerSelectRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = customerAdapter
        }
    }
    private fun setupSearch() {
        binding.searchCustomerEt.doAfterTextChanged { text ->
            wasooliViewModel.setCustomerSearchQuery(
                text.toString()
            )
        }
    }
    private fun loadData() {
        businessViewModel.loadActiveBusinessId {
            val id = businessViewModel.businessId.value

            if (id.isNullOrBlank()) {
                showError("Business not found")
                return@loadActiveBusinessId
            }
            businessId = id
            homeViewModel.observeHomeData(businessId)
            wasooliViewModel.loadReminders(businessId)
        }
    }
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.allCustomersState.collect { state ->
                        when (state) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success -> {
                                wasooliViewModel.updateCustomers(state.data)
                            }
                            is UiState.Error -> {
                                hideProgress()
                                showError(state.message)
                            }
                            is UiState.Idle -> Unit
                        }
                    }
                }
                launch {
                    wasooliViewModel.eligibleCustomersState.collect { state ->
                        when (state) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success -> {
                                customerAdapter.submitList(state.data)
                                hideProgress()
                            }
                            is UiState.Error -> {
                                hideProgress()
                                showError(state.message)
                            }
                            is UiState.Idle -> Unit
                        }
                    }
                }
            }
        }
    }
    private fun showError(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}