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
import com.example.easykhata.ui.viewModels.customer.CustomerDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class EditCustomerFragment : Fragment(R.layout.fragment_edit_customer) {
    lateinit var binding: FragmentEditCustomerBinding
    private val viewModel: CustomerDetailViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentEditCustomerBinding.bind(view)
        setUpClicks()
        observeCustomer()
    }
    private fun setUpClicks(){
        val navigateToEdit = View.OnClickListener {
            findNavController().navigate(R.id.action_editCustomerFragment_to_updateFragment)
        }
        binding.apply {
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
    private fun setUpUi(customer: Customer) {
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