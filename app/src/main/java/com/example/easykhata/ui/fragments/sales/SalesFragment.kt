package com.example.easykhata.ui.fragments.sales
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.PaymentMethod
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.sales.Sale
import com.example.easykhata.databinding.FragmentSalesBinding
import com.example.easykhata.ui.adapters.SalesAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.sales.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Locale
@AndroidEntryPoint
class SalesFragment : Fragment(R.layout.fragment_sales) {
    private lateinit var binding: FragmentSalesBinding
    private val salesViewModel: SalesViewModel by viewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private lateinit var salesAdapter: SalesAdapter
    private var allSales = emptyList<Sale>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSalesBinding.bind(view)
        setUpAdapter()
        setupUi()
        setUpClicks()
        setUpSearch()
        observeSales()
        loadSales()
    }
    private fun setupUi() {
        binding.apply {
            salesRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = salesAdapter
            }
            periodRadioGroup.check(R.id.haftaOption)
            updatePeriodLabels()
            updateSummary()
            periodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                haftaOption.setBackgroundResource(
                    if (checkedId == R.id.haftaOption) {
                        R.drawable.bg_period_selected
                    } else {
                        R.drawable.bg_period_unselected
                    }
                )
                maheenaOption.setBackgroundResource(
                    if (checkedId == R.id.maheenaOption) {
                        R.drawable.bg_period_selected
                    } else {
                        R.drawable.bg_period_unselected
                    }
                )
                updatePeriodLabels()
                updateSummary()
            }
        }
    }
    private fun updatePeriodLabels() {
        val selectedPeriod = binding.periodRadioGroup.checkedRadioButtonId
        val periodText = if (selectedPeriod == R.id.maheenaOption) {
            "Is mahine"
        } else {
            "Is hafte"
        }
        binding.apply {
            salesDate.text = periodText
            ordersDate.text = periodText
            naqadDate.text = periodText
            udhaarDate.text = periodText
        }
    }
    private fun setUpAdapter() {
        salesAdapter = SalesAdapter(
            onSaleClick = { sale ->
                val action =
                    SalesFragmentDirections.actionSalesFragmentToReciptFragment(
                        sale.id
                    )
                findNavController().navigate(action)
            },
            onSaleMenuClick = { sale, anchor ->
                // Sale menu actions will be implemented later.
            }
        )
    }
    private fun observeSales() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                salesViewModel.salesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showLoading()
                        }
                        is UiState.Success -> {
                            hideLoading()
                            allSales = state.data
                            filterSales(binding.searchEt.text.toString())
                            updateSummary()
                        }
                        is UiState.Error -> {
                            hideLoading()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UiState.Idle -> {
                            hideLoading()
                        }
                    }
                }
            }
        }
    }
    private fun setUpSearch() {
        binding.searchEt.doAfterTextChanged { editable ->
            filterSales(editable?.toString().orEmpty())
        }
    }
    private fun filterSales(query: String) {
        val searchQuery = query.trim().lowercase(Locale.ROOT)
        val filteredSales = allSales.filter { sale ->
            val customerName = sale.customerName
                .trim()
                .lowercase(Locale.ROOT)
            val displayName = if (customerName.isBlank()) {
                "walk-in customer"
            } else {
                customerName
            }
            val invoiceNumber = sale.invoiceNumber
                .trim()
                .lowercase(Locale.ROOT)
            val normalizedQuery = searchQuery.replace(",", "")
            val amount = BigDecimal.valueOf(sale.totalPrice)
                .stripTrailingZeros()
                .toPlainString()
            val originalAmount = sale.totalPrice.toString()
            searchQuery.isBlank() ||
                    displayName.contains(searchQuery) ||
                    invoiceNumber.contains(searchQuery) ||
                    amount.contains(normalizedQuery) ||
                    originalAmount.contains(normalizedQuery)
        }
        salesAdapter.submitList(filteredSales)
        binding.nataijCount.text = getString(R.string.nataij, filteredSales.size)
    }
    private fun updateSummary() {
        val periodSales = getSalesForSelectedPeriod()
        val totalSales = periodSales.sumOf { it.totalPrice }
        val naqadSales = periodSales
            .filter { it.paymentMethod == PaymentMethod.NAQAD }
            .sumOf { it.totalPrice }
        val udhaarSales = periodSales
            .filter { it.paymentMethod == PaymentMethod.UDHAAR }
            .sumOf { it.totalPrice }
        binding.apply {
            kullSalesTv.text = formatAmount(totalSales)
            ordersTv.text = periodSales.size.toString()
            naqadTv.text = formatAmount(naqadSales)
            udhaarTv.text = formatAmount(udhaarSales)
        }
    }
    private fun getSalesForSelectedPeriod(): List<Sale> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val now = System.currentTimeMillis()
        if (binding.periodRadioGroup.checkedRadioButtonId == R.id.maheenaOption) {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        } else {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val daysSinceMonday = (dayOfWeek + 5) % 7
            calendar.add(Calendar.DAY_OF_MONTH, -daysSinceMonday)
        }
        val startDate = calendar.timeInMillis
        return allSales.filter { sale ->
            sale.date in startDate..now
        }
    }
    private fun formatAmount(amount: Double): String {
        val formatter = DecimalFormat("#,##0.##")
        return "Rs. ${formatter.format(amount)}"
    }
    private fun setUpClicks() {
        binding.apply {
            itemsManageCard.setOnClickListener {
                findNavController().navigate(
                    R.id.action_salesFragment_to_itemsManageFragment
                )
            }
            saleDarjKareinButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_salesFragment_to_saleDarjkareinFragment
                )
            }
        }
    }
    private fun loadSales() {
        val businessId = businessViewModel.businessId.value.orEmpty()
        if (businessId.isNotBlank()) {
            salesViewModel.observeSales(businessId)
        } else {
            Toast.makeText(
                requireContext(),
                "Please select a business first",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun showLoading() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideLoading() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}