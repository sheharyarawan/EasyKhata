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
import androidx.navigation.fragment.navArgs
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.databinding.FragmentCustomerDetailBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.CustomerDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
@AndroidEntryPoint
class CustomerDetailFragment : Fragment(R.layout.fragment_customer_detail) {
    lateinit var binding: FragmentCustomerDetailBinding
    private val viewModel: CustomerDetailViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val args: CustomerDetailFragmentArgs by navArgs()
    private lateinit var customerId: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentCustomerDetailBinding.bind(view)
        customerId = args.customerId
        setUpClicks()
        observeCustomer()
        loadCustomer()
    }
    private fun setUpClicks() {
        binding.apply {
            customerDetailToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            customerDetailToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.addToFav -> {
                        // Add or remove customer from favourites
                        true
                    }
                    R.id.editCustomerToolbar -> {
                       findNavController().navigate(R.id.action_customerDetailFragment_to_editCustomerFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }
    private fun loadCustomer() {
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank() || customerId.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Business or customer not selected",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        viewModel.loadCustomer(businessId, customerId)
    }
    private fun observeCustomer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.customerState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            setUpToolbar(state.data)
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UiState.Idle -> {
                            hideProgress()
                        }
                    }
                }
            }
        }
    }
    private fun setUpToolbar(customer: Customer) {
        binding.apply {
            customerDetailToolbar.title = customer.name
            val formattedDate = customer.createdAt?.toDate()?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
            }
            customerDetailToolbar.subtitle =
                formattedDate?.let { "Customer since $it" } ?: "Date unavailable"
        }
    }
    private fun showProgress() {
        binding.customerDetailProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerDetailProgressOverlay.visibility = View.GONE
    }
}