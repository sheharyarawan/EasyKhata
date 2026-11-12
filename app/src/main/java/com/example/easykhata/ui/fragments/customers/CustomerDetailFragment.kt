package com.example.easykhata.ui.fragments.customers
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.databinding.FragmentCustomerDetailBinding
import com.example.easykhata.ui.adapters.ExpenseAdapter
import com.example.easykhata.ui.fragments.expense.bottomSheets.ExpenseFilterBottomSheet
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.customer.CustomerDetailViewModel
import com.example.easykhata.ui.viewModels.expense.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged

@AndroidEntryPoint
class CustomerDetailFragment : Fragment(R.layout.fragment_customer_detail) {
    lateinit var binding: FragmentCustomerDetailBinding
    private val viewModel: CustomerDetailViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private lateinit var expenseAdapter: ExpenseAdapter
    private val args: CustomerDetailFragmentArgs by navArgs()
    private lateinit var customerId: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCustomerDetailBinding.bind(view)
        customerId = args.customerId
        setUpClicks()
        setUpExpenseRecyclerView()
        observeCustomer()
        observeCustomerExpenses()
        observeRunningBalance()
        loadCustomer()
        loadCustomerExpenses()
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
            diyeButton.setOnClickListener {
                val action =
                    CustomerDetailFragmentDirections.actionCustomerDetailFragmentToAddExpenseFragment(
                        args.customerId, ExpenseDirection.DIYE.name
                    )
                findNavController().navigate(action)
            }
            liyeButton.setOnClickListener {
                val action =
                    CustomerDetailFragmentDirections.actionCustomerDetailFragmentToAddExpenseFragment(
                        args.customerId, ExpenseDirection.LIYE.name
                    )
                findNavController().navigate(action)
            }
            cardSort.setOnClickListener {
                ExpenseFilterBottomSheet().show(
                    childFragmentManager,
                    "ExpenseFilterBottomSheet"
                )
            }
            cardSearch.setOnClickListener {
                binding.searchCardView.visibility =
                    if (searchCardView.isVisible) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            }
            etSearchCustomerExpand.doAfterTextChanged { editable ->
                expenseViewModel.setSearchQuery(editable.toString())
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
    private fun setUpExpenseRecyclerView() {
        expenseAdapter = ExpenseAdapter{expense ->
            val action= CustomerDetailFragmentDirections.actionCustomerDetailFragmentToExpenseDetailFragment(
                expense
            )
            findNavController().navigate(action)
        }
        binding.customerDetailRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }
    }
    private fun observeCustomerExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.customerExpensesState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            expenseAdapter.submitList(state.data)
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
    private fun loadCustomerExpenses() {
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank() || customerId.isBlank()) {
            return
        }
        expenseViewModel.loadCustomerExpenses(
            businessId = businessId,
            customerId = customerId
        )
    }
    private fun observeRunningBalance() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.runningBalance.collect { balance ->
                    updateHisaabStatus(balance)
                }
            }
        }
    }
    private fun updateHisaabStatus(balance: Double) {
        binding.apply {
            val amount = DecimalFormat("#,##0.##").format(kotlin.math.abs(balance))
            tvAmount.text = getString(R.string.amount_rupees, amount)
            when {
                balance > 0 -> {
                    tvHisaabStatus.setText(R.string.hisaab_lene_hain)
                    tvAmount.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                    tvHisaabStatus.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                    cardHisaabStatus.setBackgroundResource(R.drawable.bg_red)
                    ivDropdown.setBackgroundResource(R.drawable.circle_red)
                    ivDropdown.setImageResource(R.drawable.ic_arrow_downward)
                }
                balance < 0 -> {
                    tvHisaabStatus.setText(R.string.hisaab_dene_hain)
                    tvAmount.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.green)
                    )
                    tvHisaabStatus.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.green)
                    )
                    cardHisaabStatus.setBackgroundResource(R.drawable.bg_green)
                    ivDropdown.setBackgroundResource(R.drawable.circle_green)
                    ivDropdown.setImageResource(R.drawable.ic_arrow_upward)
                }
                else -> {
                    tvAmount.setText(R.string.hisaab_zero)
                    tvHisaabStatus.setText(R.string.hisaab_clear_hai)
                    val gray = ContextCompat.getColor(requireContext(), R.color.lt_gray)
                    val black= ContextCompat.getColor(requireContext(),R.color.black)
                    tvAmount.setTextColor(black)
                    tvHisaabStatus.setTextColor(black)
                    cardHisaabStatus.setCardBackgroundColor(gray)
                    ivDropdown.setBackgroundResource(R.drawable.circle_gray)
                }
            }
        }
    }
    private fun showProgress() {
        binding.customerDetailProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerDetailProgressOverlay.visibility = View.GONE
    }
}