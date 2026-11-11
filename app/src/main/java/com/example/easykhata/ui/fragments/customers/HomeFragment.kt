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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.FragmentHomeBinding
import com.example.easykhata.ui.adapters.CustomerAdapter
import com.example.easykhata.ui.fragments.customers.bottomSheets.CustomerFilterBottomSheet
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.getValue
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var binding: FragmentHomeBinding
    private lateinit var customerAdapter: CustomerAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentHomeBinding.bind(view)
        setUpClicks()
        setUpRecyclerView()
        observeHomeState()
        setUpUi()
        loadHomeData()
    }
    private fun setUpClicks(){
        binding.apply {
            searchCardView.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchCustomerFragment)
            }
            addCustomerButton.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_addCustomerFragment)
            }
            cardPdf.setOnClickListener {
                val action= HomeFragmentDirections.actionHomeFragmentToCustomerDetailFragment("MEVGCItWG5OtrexIE6wH")
                findNavController().navigate(action)
            }
            cardSort.setOnClickListener {
                CustomerFilterBottomSheet().show(
                    parentFragmentManager,
                    "CustomerFilterBottomSheet"
                )
            }
        }
    }
    private fun setUpUi(){
        observeTotalLeneDene()
    }
    private fun observeHomeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.customersState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            val customers = state.data
                            binding.addCustomerTv.visibility =
                                if (customers.isEmpty()) View.VISIBLE else View.GONE
                            binding.show.visibility =
                                if (customers.isEmpty()) View.GONE else View.VISIBLE
                            customerAdapter.submitList(customers)
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
    private fun observeTotalLeneDene() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.totalLene.collect { amount ->
                        binding.homeLene.text = formatAmount(amount)
                    }
                }
                launch {
                    homeViewModel.totalDene.collect { amount ->
                        binding.homeDene.text = formatAmount(amount)
                    }
                }
            }
        }
    }
    private fun setUpRecyclerView() {
        customerAdapter = CustomerAdapter { customerId ->
            val action =
                HomeFragmentDirections
                    .actionHomeFragmentToCustomerDetailFragment(customerId)
            findNavController().navigate(action)
        }
        binding.customerRvHome.apply {
            layoutManager= LinearLayoutManager(requireContext())
            adapter = customerAdapter
        }
    }
    private fun loadHomeData() {
        val businessId = businessViewModel.businessId.value
        if (!businessId.isNullOrBlank()) {
            homeViewModel.observeHomeData(businessId)
        } else {
            businessViewModel.loadActiveBusinessId {
                val id = businessViewModel.businessId.value
                if (!id.isNullOrBlank()) {
                    homeViewModel.observeHomeData(id)
                }
            }
        }
    }
    private fun formatAmount(amount: Double): String {
        return String.format(Locale.getDefault(), "Rs %,.0f", amount)
    }
    private fun showProgress() {
        binding.customerDetailProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerDetailProgressOverlay.visibility = View.GONE
    }
}