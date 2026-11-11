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
import com.example.easykhata.data.models.customers.KhataReportItem
import com.example.easykhata.databinding.FragmentKhataReportBinding
import com.example.easykhata.ui.adapters.KhataReportAdapter
import com.example.easykhata.ui.fragments.customers.bottomSheets.KhataBottomSheet
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.HomeViewModel
import com.example.easykhata.ui.viewModels.expense.KhataReportViewModel
import com.example.easykhata.utils.showPastDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
@AndroidEntryPoint
class KhataReportFragment : Fragment(R.layout.fragment_khata_report) {
    lateinit var binding: FragmentKhataReportBinding
    private val khataReportViewModel: KhataReportViewModel by activityViewModels()
    private lateinit var khataReportAdapter: KhataReportAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentKhataReportBinding.bind(view)
        setUpClicks()
        loadReportData()
        setupRecyclerView()
        observeSelectedDates()
        observeSingleDayPicker()
    }
    private fun setUpClicks(){
        binding.apply {
            khataReportToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            khataAll.setOnClickListener {
                KhataBottomSheet().show(
                    parentFragmentManager,
                    "KhataFilterBottomSheet"
                )
            }
            binding.khataStartDate.setOnClickListener {
                showPastDatePicker { date ->
                    khataReportViewModel.setStartDate(date)
                }
            }
            binding.khataEndDate.setOnClickListener {
                showPastDatePicker { date ->
                    khataReportViewModel.setEndDate(date)
                }
            }
        }
    }
    private fun loadReportData() {
        observeReportState()
        observeReportTotals()
        businessViewModel.loadActiveBusinessId {
            val businessId = businessViewModel.businessId.value
            if (!businessId.isNullOrBlank()) {
                khataReportViewModel.observeReportExpenses(businessId)
            }
        }
    }
    private fun observeReportState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                khataReportViewModel.reportState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            val expenses = state.data
                            val customers = homeViewModel.allCustomersState.value
                            if (customers is UiState.Success) {
                                val customerMap = customers.data.associate {
                                    it.customer.id to it.customer.name
                                }
                                val reportItems = expenses.map { expense ->
                                    KhataReportItem(
                                        expense = expense,
                                        customerName = customerMap[expense.customerId] ?: "Unknown"
                                    )
                                }
                                khataReportAdapter.submitList(reportItems)
                            }
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
    private fun observeReportTotals() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    khataReportViewModel.totalDiye.collect { total ->
                        binding.khataReportDiye.text = getString(
                            R.string.amount_rs,
                            "%,.0f".format(total)
                        )
                    }
                }
                launch {
                    khataReportViewModel.totalLiye.collect { total ->
                        binding.khataReportDiye.text = getString(
                            R.string.amount_rs,
                            "%,.0f".format(total)
                        )
                    }
                }
            }
        }
    }
    private fun observeSelectedDates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    khataReportViewModel.selectedStartDate.collect { date ->
                        binding.khataStartDate.text =
                            date?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) }
                                ?: "Start Date"
                    }
                }
                launch {
                    khataReportViewModel.selectedEndDate.collect { date ->
                        binding.khataEndDate.text =
                            date?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) }
                                ?: "End Date"
                    }
                }
            }
        }
    }
    private fun observeSingleDayPicker() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                khataReportViewModel.openSingleDayPicker.collect { shouldOpen ->
                    if (shouldOpen) {
                        khataReportViewModel.resetSingleDayPicker()
                        showPastDatePicker { date ->
                            khataReportViewModel.setSingleDay(date)
                        }
                    }
                }
            }
        }
    }
    private fun setupRecyclerView() {
        khataReportAdapter = KhataReportAdapter()
        binding.khataRecyclerViw.apply {
            layoutManager= LinearLayoutManager(requireContext())
            adapter = khataReportAdapter
        }
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}