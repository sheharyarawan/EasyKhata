package com.example.easykhata.ui.fragments.expense
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.easykhata.R
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.expense.Expense
import com.example.easykhata.databinding.FragmentAddExpenseBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.expense.ExpenseViewModel
import com.example.easykhata.utils.formatDate
import com.example.easykhata.utils.showPastDatePicker
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {
    private lateinit var binding: FragmentAddExpenseBinding
    private lateinit var expenseDirection: ExpenseDirection
    private var expenseTimestamp: Timestamp = Timestamp.now()
    private val args: AddExpenseFragmentArgs by navArgs()
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddExpenseBinding.bind(view)
        expenseDirection = ExpenseDirection.valueOf(args.direction)
        setUpUi()
        setUpClicks()
        observeAddExpenseState()
    }
    private fun setUpClicks() {
        binding.apply {
            expenseToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            expenseSave.setOnClickListener {
                addExpense()
            }
            detailExpenseDateCard.setOnClickListener {
                showPastDatePicker { selectedDate ->
                    expenseTimestamp = Timestamp(selectedDate)
                    detailExpenseDate.text =
                        formatDate(selectedDate)
                }
            }
        }
    }
    private fun setUpUi() {
        setUpExpenseDirectionUi()
        setUpExpenseDate()
    }
    private fun addExpense() {
        val amount = binding.expenseAmount.text.toString().toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            binding.expenseAmount.error = "Enter a valid amount"
            return
        }
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Please select a business first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val expense = Expense(
            customerId = args.customerId,
            date = expenseTimestamp,
            description = binding.etDescription.text.toString().trim(),
            amount = amount,
            direction = expenseDirection
        )
        expenseViewModel.addExpense(
            businessId = businessId,
            expense = expense
        )
    }
    private fun observeAddExpenseState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.addExpenseState.collect { state ->
                    when (state) {
                        null -> hideProgress()
                        is UiState.Idle -> {
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            expenseViewModel.resetAddExpenseState()
                            findNavController().navigateUp()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            expenseViewModel.resetAddExpenseState()
                        }
                    }
                }
            }
        }
    }
    private fun setUpExpenseDirectionUi() {
        binding.apply {
            when (expenseDirection) {
                ExpenseDirection.DIYE -> {
                    expenseToolbar.title = "Maine diye"
                    setExpenseSaveButtonColor(R.color.red)
                }

                ExpenseDirection.LIYE -> {
                    expenseToolbar.title = "Maine liye"
                    setExpenseSaveButtonColor(R.color.green)
                }
            }
        }
    }
    private fun setExpenseSaveButtonColor(color: Int) {
        binding.expenseSave.backgroundTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), color)
            )
    }
    private fun setUpExpenseDate() {
        binding.detailExpenseDate.text =
            formatDate(expenseTimestamp.toDate())
    }
    private fun showProgress() {
        binding.expenseProgressOverlay.visibility = View.VISIBLE
        binding.expenseSave.isEnabled = false
    }
    private fun hideProgress() {
        binding.expenseProgressOverlay.visibility = View.GONE
        binding.expenseSave.isEnabled = true
    }
}
