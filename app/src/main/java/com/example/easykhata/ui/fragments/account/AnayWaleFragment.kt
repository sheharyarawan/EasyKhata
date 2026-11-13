package com.example.easykhata.ui.fragments.account
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
import com.example.easykhata.data.models.account.WasooliCustomerItem
import com.example.easykhata.data.models.account.WasooliReminder
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.databinding.FragmentAnayWaleBinding
import com.example.easykhata.ui.adapters.WasooliAdapter
import com.example.easykhata.ui.fragments.customers.bottomSheets.WasooliBottomSheet
import com.example.easykhata.ui.viewModels.account.WasooliViewModel
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class AnayWaleFragment : Fragment(R.layout.fragment_anay_wale) {
    private lateinit var binding: FragmentAnayWaleBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val wasooliViewModel: WasooliViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private lateinit var wasooliAdapter: WasooliAdapter
    private var customers = emptyList<CustomerWithBalance>()
    private var reminders = emptyList<WasooliReminder>()
    private var businessId = ""
    private var customersLoaded = false
    private var remindersLoaded = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAnayWaleBinding.bind(view)
        setupRecyclerView()
        setUpClicks()
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
        binding.udhaarWasooliRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wasooliAdapter
        }
    }
    private fun setUpClicks() {
        binding.udhaarWasooliTareekhButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_wasooliFragment_to_wasooliCustomerFragment
            )
        }
    }
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.allCustomersState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                customersLoaded = false
                                updateProgress()
                            }
                            is UiState.Success -> {
                                customers = state.data
                                customersLoaded = true
                                updateProgress()
                                updateReminderList()
                            }
                            is UiState.Error -> {
                                customersLoaded = false
                                hideProgress()
                                showError(state.message)
                            }
                            is UiState.Idle -> Unit
                        }
                    }
                }
                launch {
                    wasooliViewModel.upcomingRemindersState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                remindersLoaded = false
                                updateProgress()
                            }
                            is UiState.Success -> {
                                reminders = state.data
                                remindersLoaded = true
                                updateProgress()
                                updateReminderList()
                            }
                            is UiState.Error -> {
                                remindersLoaded = false
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
    private fun showError(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun updateProgress() {
        if (customersLoaded && remindersLoaded) {
            hideProgress()
        } else {
            showProgress()
        }
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}