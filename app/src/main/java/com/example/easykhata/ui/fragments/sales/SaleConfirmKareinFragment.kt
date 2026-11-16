package com.example.easykhata.ui.fragments.sales
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.PaymentMethod
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.data.models.sales.Sale
import com.example.easykhata.data.models.sales.SaleItemDetail
import com.example.easykhata.data.models.sales.SelectedSaleItem
import com.example.easykhata.databinding.FragmentSaleConfirmKareinBinding
import com.example.easykhata.ui.adapters.SaleConfirmAdapter
import com.example.easykhata.ui.adapters.SaleCustomerAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.HomeViewModel
import com.example.easykhata.ui.viewModels.sales.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@AndroidEntryPoint
class SaleConfirmKareinFragment :
    Fragment(R.layout.fragment_sale_confirm_karein) {
    private lateinit var binding: FragmentSaleConfirmKareinBinding
    private val salesViewModel: SalesViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: SaleConfirmAdapter
    private lateinit var customerAdapter: SaleCustomerAdapter
    private var selectedPaymentMethod = PaymentMethod.NAQAD
    private var selectedItems: List<SelectedSaleItem> = emptyList()
    private var allCustomers: List<CustomerWithBalance> = emptyList()
    private var selectedCustomer: Customer? = null
    private var subTotal = 0.0
    private var discount = 0.0
    private var totalAmount = 0.0
    private val decimalFormat = DecimalFormat("#,##0.##")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSaleConfirmKareinBinding.bind(view)
        setUpRecyclerView()
        setUpCustomerRecyclerView()
        setUpClicks()
        setDefaultPaymentMethod()
        observeSelectedItems()
        observeCustomers()
        observeAddSaleState()
        loadCustomers()
        calculateTotal()
    }
    private fun setUpRecyclerView() {
        adapter = SaleConfirmAdapter()
        binding.saleConfirmRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SaleConfirmKareinFragment.adapter
            isNestedScrollingEnabled = false
        }
    }
    private fun setUpCustomerRecyclerView() {
        customerAdapter = SaleCustomerAdapter(::selectCustomer)
        binding.customerSearchRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = customerAdapter
            isNestedScrollingEnabled = false
        }
    }
    private fun setUpClicks() {
        binding.apply {
            saleConfirmToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            naqadCardView.setOnClickListener {
                selectPaymentMethod(PaymentMethod.NAQAD)
            }
            udhaarCardView.setOnClickListener {
                selectPaymentMethod(PaymentMethod.UDHAAR)
            }
            discountEt.doAfterTextChanged {
                calculateTotal()
            }
            searchCustomerEt.doAfterTextChanged {
                filterCustomers(it?.toString().orEmpty())
            }
            searchCustomerEt.setOnClickListener {
                if (
                    selectedPaymentMethod == PaymentMethod.UDHAAR &&
                    selectedCustomer == null
                ) {
                    filterCustomers(searchCustomerEt.text.toString())
                }
            }
            selectedCustomerTv.setOnClickListener {
                clearSelectedCustomer()
            }
            saleConfirmButton.setOnClickListener {
                confirmSale()
            }
        }
    }
    private fun setDefaultPaymentMethod() {
        selectPaymentMethod(PaymentMethod.NAQAD)
    }
    private fun selectPaymentMethod(method: PaymentMethod) {
        selectedPaymentMethod = method
        val naqadSelected = method == PaymentMethod.NAQAD
        binding.apply {
            naqadCardView.setCardBackgroundColor(
                requireContext().getColor(
                    if (naqadSelected) R.color.black else R.color.white
                )
            )
            udhaarCardView.setCardBackgroundColor(
                requireContext().getColor(
                    if (!naqadSelected) R.color.black else R.color.white
                )
            )
            textView46.setTextColor(
                requireContext().getColor(
                    if (naqadSelected) R.color.white else R.color.black
                )
            )
            textView50.setTextColor(
                requireContext().getColor(
                    if (!naqadSelected) R.color.white else R.color.black
                )
            )
            imageView5.setColorFilter(
                requireContext().getColor(
                    if (naqadSelected) R.color.white else R.color.gray
                )
            )
            imageView6.setColorFilter(
                requireContext().getColor(
                    if (!naqadSelected) R.color.white else R.color.gray
                )
            )
            customerTv.visibility =
                if (naqadSelected) View.GONE else View.VISIBLE
            if (naqadSelected) {
                clearSelectedCustomer()
            } else if (selectedCustomer == null) {
                searchCustomerCard.visibility = View.VISIBLE
                customerSearchRv.visibility = View.GONE
                selectedCustomerTv.visibility = View.GONE
            }
        }
    }
    private fun loadCustomers() {
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank()) return
        if (homeViewModel.allCustomersState.value is UiState.Idle) {
            homeViewModel.observeHomeData(businessId)
        }
    }
    private fun observeCustomers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.allCustomersState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            allCustomers = state.data
                            if (
                                selectedPaymentMethod == PaymentMethod.UDHAAR &&
                                selectedCustomer == null &&
                                binding.searchCustomerEt.hasFocus()
                            ) {
                                filterCustomers(
                                    binding.searchCustomerEt.text.toString()
                                )
                            }
                        }
                        is UiState.Error -> {
                            showToast(state.message)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
    private fun filterCustomers(query: String) {
        if (
            selectedPaymentMethod != PaymentMethod.UDHAAR ||
            selectedCustomer != null
        ) {
            binding.customerSearchRv.visibility = View.GONE
            return
        }
        val filteredCustomers = allCustomers.filter { customerWithBalance ->
            val customer = customerWithBalance.customer
            customer.name.contains(query, ignoreCase = true) ||
                    customer.phone.contains(query, ignoreCase = true)
        }
        customerAdapter.submitList(filteredCustomers)
        binding.customerSearchRv.visibility =
            if (filteredCustomers.isEmpty()) View.GONE else View.VISIBLE
    }
    private fun selectCustomer(customer: Customer) {
        selectedCustomer = customer
        binding.apply {
            selectedCustomerTv.text = customer.name
            searchCustomerCard.visibility = View.GONE
            customerSearchRv.visibility = View.GONE
            selectedCustomerTv.visibility = View.VISIBLE
        }
    }
    private fun clearSelectedCustomer() {
        selectedCustomer = null
        binding.apply {
            searchCustomerEt.text?.clear()
            selectedCustomerTv.visibility = View.GONE
            customerSearchRv.visibility = View.GONE
            if (selectedPaymentMethod == PaymentMethod.UDHAAR) {
                searchCustomerCard.visibility = View.VISIBLE
                customerTv.visibility = View.VISIBLE
            } else {
                searchCustomerCard.visibility = View.GONE
                customerTv.visibility = View.GONE
            }
        }
        customerAdapter.submitList(emptyList())
    }
    private fun observeSelectedItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                salesViewModel.selectedSaleItems.collect { items ->
                    selectedItems = items
                    adapter.submitList(items.toList())
                    binding.saleItemCount.text = getString(R.string.items_2, items.size)
                    calculateTotal()
                }
            }
        }
    }
    private fun calculateTotal() {
        subTotal = selectedItems.sumOf { selectedItem ->
            selectedItem.item.price * selectedItem.quantity
        }
        discount = binding.discountEt.text
            .toString()
            .toDoubleOrNull() ?: 0.0
        totalAmount = subTotal - discount
        binding.apply {
            saleSubTotal.text =
                getString(R.string.rs_7, decimalFormat.format(subTotal))
            kullRaqam.text =
                getString(R.string.rs_9, decimalFormat.format(totalAmount))
        }
    }
    private fun confirmSale() {
        calculateTotal()
        if (selectedItems.isEmpty()) {
            showToast("Please add at least one item")
            return
        }
        if (discount !in 0.0..subTotal) {
            binding.discountEt.error = "Enter a valid discount"
            return
        }
        if (
            selectedPaymentMethod == PaymentMethod.UDHAAR &&
            selectedCustomer == null
        ) {
            showToast("Please select a customer")
            return
        }
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank()) {
            showToast("Business not selected")
            return
        }
        val saleItems = selectedItems.map { selectedItem ->
            val item = selectedItem.item
            val quantity = selectedItem.quantity.toDouble()
            SaleItemDetail(
                itemId = item.id,
                itemName = item.name,
                quantity = quantity,
                unit = item.unit,
                unitSymbol = item.unitSymbol,
                price = item.price,
                total = item.price * quantity
            )
        }
        val currentDate = System.currentTimeMillis()
        val currentTime = SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date(currentDate))
        val sale = Sale(
            customerId = if (selectedPaymentMethod == PaymentMethod.UDHAAR) {
                selectedCustomer?.id.orEmpty()
            } else {
                ""
            },
            customerName = if (selectedPaymentMethod == PaymentMethod.UDHAAR) {
                selectedCustomer?.name.orEmpty()
            } else {
                ""
            },
            items = saleItems,
            date = currentDate,
            time = currentTime,
            totalPrice = totalAmount,
            discount = discount,
            paymentMethod = selectedPaymentMethod,
            note = binding.noteLikheinEt.text.toString().trim()
        )
        salesViewModel.addSale(
            businessId = businessId,
            sale = sale
        )
    }
    private fun observeAddSaleState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                salesViewModel.addSaleState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            val saleId = state.data
                            salesViewModel.clearSelectedSaleItems()
                            salesViewModel.resetAddSaleState()
                            val action= SaleConfirmKareinFragmentDirections.actionSaleConfirmKareinFragmentToReciptFragment(
                                saleId
                            )
                            findNavController().navigate(action)
                        }
                        is UiState.Error -> {
                            hideProgress()
                            showToast(state.message)
                            salesViewModel.resetAddSaleState()
                        }
                    }
                }
            }
        }
    }
    private fun showProgress() {
        binding.customerDetailProgressOverlay.visibility = View.VISIBLE
        binding.saleConfirmButton.isEnabled = false
    }
    private fun hideProgress() {
        binding.customerDetailProgressOverlay.visibility = View.GONE
        binding.saleConfirmButton.apply {
            isEnabled = true
            text = getString(R.string.sale_confirm_karein)
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}