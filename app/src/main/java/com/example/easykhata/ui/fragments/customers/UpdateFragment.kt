package com.example.easykhata.ui.fragments.customers
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.databinding.FragmentUpdateBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.CustomerDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
@AndroidEntryPoint
class UpdateFragment : Fragment(R.layout.fragment_update) {
    lateinit var binding: FragmentUpdateBinding
    private val viewModel: CustomerDetailViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private var isSaving = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentUpdateBinding.bind(view)
        setUpClicks()
        observeCustomer()
    }
    private fun setUpClicks(){
        binding.apply {
            updateToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            saveUpdate.setOnClickListener {
                saveCustomerChanges()
            }
        }
    }
    private fun observeCustomer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.customerState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            setUpUi(state.data)
                            if (isSaving) {
                                isSaving = false
                                findNavController().navigateUp()
                            }
                        }
                        is UiState.Error -> {
                            hideProgress()
                            isSaving = false
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
    private fun setUpUi(customer: Customer) {
        binding.apply {
            editTextNameUpdate.setText(customer.name)
            editTextNumberUpdate.setText(customer.phone)
        }
    }
    private fun saveCustomerChanges() {
        val currentState = viewModel.customerState.value
        if (currentState !is UiState.Success) {
            Toast.makeText(
                requireContext(),
                "Customer information not available",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val currentCustomer = currentState.data
        val updatedCustomer = currentCustomer.copy(
            name = binding.editTextNameUpdate.text.toString().trim(),
            phone = binding.editTextNumberUpdate.text.toString().trim()
        )
        if (updatedCustomer.name.isBlank()) {
            binding.editTextNameUpdate.error = "Enter customer name"
            return
        }
        if (updatedCustomer.phone.isNotEmpty()) {
            val phone = updatedCustomer.phone
            if (phone.length != 11 ||
                !phone.startsWith("03") ||
                !phone.all { it.isDigit() }
            ) {
                binding.editTextNumberUpdate.error =
                    "Durust phone number darj karein"
                binding.editTextNumberUpdate.requestFocus()
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
        isSaving = true
        viewModel.updateCustomer(
            businessId = businessId,
            customer = updatedCustomer
        )
    }
    private fun showProgress() {
        binding.updateProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.updateProgressOverlay.visibility = View.GONE
    }
}