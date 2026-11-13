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
import com.example.easykhata.databinding.FragmentEditCustomerBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.CustomerDetailViewModel
import com.example.easykhata.utils.showConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class EditCustomerFragment : Fragment(R.layout.fragment_edit_customer) {
    lateinit var binding: FragmentEditCustomerBinding
    private val viewModel: CustomerDetailViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    lateinit var customerId: String
    lateinit var customerName:String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentEditCustomerBinding.bind(view)
        setUpClicks()
        observeCustomer()
        observeDeleteCustomer()
    }
    private fun setUpClicks(){
        val navigateToEdit = View.OnClickListener {
            findNavController().navigate(R.id.action_editCustomerFragment_to_updateFragment)
        }
        binding.apply {
            editCustomerDelete.setOnClickListener {
                delete()
            }
            editCustomerToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            editTextNameUpdate.setOnClickListener(navigateToEdit)
            editTextNumberUpdate.setOnClickListener(navigateToEdit)
            editCustomerSupplier.setOnClickListener(navigateToEdit)
            editCustomerEdit.setOnClickListener(navigateToEdit)
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
    private fun observeDeleteCustomer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteCustomerState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                "Customer deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetDeleteCustomerState()
                            findNavController().popBackStack(
                                R.id.homeFragment,
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
                            viewModel.resetDeleteCustomerState()
                        }
                    }
                }
            }
        }
    }
    private fun delete() {
        val businessId = businessViewModel.businessId.value?: return
        val customerId = customerId
        if (businessId.isBlank() || customerId.isBlank()) {
            Toast.makeText(requireContext()
                ,"Customer Id or Business Id not selected", Toast.LENGTH_SHORT).show()
        }
        showConfirmationDialog(
            title = "Customer delete karein?",
            message = "Kya aap $customerName ka khata delete karna chahte hain?"
        ) {
            viewModel.deleteCustomer(
                businessId = businessId,
                customerId = customerId
            )
        }
    }
    private fun setUpUi(customer: Customer) {
        customerId=customer.id
        customerName=customer.name
        binding.apply {
            editTextNameUpdate.text=customer.name
            editTextNumberUpdate.text=customer.phone
            editCustomerToolbar.title=customer.name
        }
    }
    private fun showProgress() {
        binding.editCustomerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.editCustomerProgressOverlay.visibility = View.GONE
    }
}