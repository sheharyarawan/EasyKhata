package com.example.easykhata.ui.fragments.customers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.databinding.FragmentAddCustomerDetailBinding
import com.example.easykhata.ui.viewModels.BusinessViewModel
import com.example.easykhata.ui.viewModels.customerViewModel.AddCustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class AddCustomerDetailFragment : Fragment(R.layout.fragment_add_customer_detail) {
    lateinit var binding: FragmentAddCustomerDetailBinding
    private val viewModel: AddCustomerViewModel by viewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentAddCustomerDetailBinding.bind(view)
        setUpClicks()
        observeAddCustomerState()
    }
    private fun setUpClicks(){
        binding.apply {
            addCustomerDetailToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            addCustomerDetailButton.setOnClickListener {
                addCustomer()
            }
        }
    }
    private fun addCustomer() {
        val name = binding.addCustomerNameEt.text.toString().trim()
        val phone = binding.addCustomerPhoneEt.text.toString().trim()
        if (name.isEmpty()) {
            binding.addCustomerNameEt.error = "Please enter customer name"
            binding.addCustomerNameEt.requestFocus()
            return
        }
        if (phone.isNotEmpty()) {
            if (phone.length != 11 ||
                !phone.startsWith("03") ||
                !phone.all { it.isDigit() }
            ) {
                binding.addCustomerPhoneEt.error =
                    "Durust phone number darj karein"
                binding.addCustomerPhoneEt.requestFocus()
                return
            }
        }
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Please select a business first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val customer = Customer(
            name = name,
            phone = phone
        )
        viewModel.addCustomer(businessId, customer)
    }
    private fun observeAddCustomerState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addCustomerState.collect { state ->
                    when (state) {
                        null -> hideProgress()
                        is UiState.Idle -> hideProgress()
                        is UiState.Loading -> showProgress()
                        is UiState.Success -> {
                            hideProgress()
                            val customerId = state.data
                            viewModel.resetAddCustomerState()
                            val action =
                                AddCustomerDetailFragmentDirections
                                    .actionAddCustomerDetailFragmentToCustomerDetailFragment(
                                        customerId
                                    )
                            findNavController().navigate(action)
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetAddCustomerState()
                        }
                    }
                }
            }
        }
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}