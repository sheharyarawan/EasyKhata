package com.example.easykhata.ui.fragments.account.bottomSheets
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.account.WasooliCustomerItem
import com.example.easykhata.data.models.account.WasooliReminder
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.databinding.FragmentLateWaleBinding
import com.example.easykhata.ui.adapters.WasooliAdapter
import com.example.easykhata.ui.fragments.customers.bottomSheets.WasooliBottomSheet
import com.example.easykhata.ui.viewModels.account.WasooliViewModel
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class LateWaleFragment : Fragment(R.layout.fragment_late_wale) {
    private lateinit var binding: FragmentLateWaleBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val wasooliViewModel: WasooliViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private lateinit var wasooliAdapter: WasooliAdapter
    private var businessId = ""
    private var customers = emptyList<CustomerWithBalance>()
    private var reminders = emptyList<WasooliReminder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLateWaleBinding.bind(view)
        setupRecyclerView()
        observeData()
        loadData()
    }
    private fun setupRecyclerView() {
        wasooliAdapter = WasooliAdapter { item ->
            WasooliBottomSheet.newInstance(
                customerId = item.reminder.customerId,
                customerName = item.customerWithBalance.customer.name,
                businessId = businessId
            ).show(parentFragmentManager, "WasooliBottomSheet")
        }
        binding.lateWaleRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wasooliAdapter
        }
    }
    private fun loadData() {
        businessViewModel.loadActiveBusinessId {
            val id = businessViewModel.businessId.value
            if (id.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Business not selected",
                    Toast.LENGTH_SHORT
                ).show()
                return@loadActiveBusinessId
            }
            businessId = id
            homeViewModel.observeHomeData(id)
            wasooliViewModel.loadReminders(id)
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
                                customers = state.data
                                updateReminderList()
                            }
                            is UiState.Error -> {
                                hideProgress()
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is UiState.Idle -> Unit
                        }
                    }
                }
                launch {
                    wasooliViewModel.lateRemindersState.collect { state ->
                        when (state) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success -> {
                                reminders = state.data
                                updateReminderList()
                                hideProgress()
                            }
                            is UiState.Error -> {
                                hideProgress()
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is UiState.Idle -> Unit
                        }
                    }
                }
            }
        }
    }
    private fun updateReminderList() {
        val customerMap = customers.associateBy {
            it.customer.id
        }
        val items = reminders.mapNotNull { reminder ->
            val customer = customerMap[reminder.customerId]
            if (customer != null && customer.balance > 0.0) {
                WasooliCustomerItem(
                    customerWithBalance = customer,
                    reminder = reminder
                )
            } else {
                null
            }
        }
        wasooliAdapter.submitList(items)
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}