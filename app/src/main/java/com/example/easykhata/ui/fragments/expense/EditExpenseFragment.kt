package com.example.easykhata.ui.fragments.expense
import android.os.Bundle
import android.view.View
import android.content.res.ColorStateList
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.easykhata.R
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.FragmentEditExpenseBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.expense.ExpenseViewModel
import com.example.easykhata.utils.formatDate
import com.example.easykhata.utils.showPastDatePicker
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class EditExpenseFragment : Fragment(R.layout.fragment_edit_expense) {
    private lateinit var binding: FragmentEditExpenseBinding
    private val args: EditExpenseFragmentArgs by navArgs()
    private var expenseTimestamp: Timestamp = Timestamp.now()
    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditExpenseBinding.bind(view)
        setUpUi()
        setUpClicks()
        observeUpdateExpenseState()
    }
    private fun setUpUi() {
        val expense = args.expense
        expenseTimestamp = expense.date ?: Timestamp.now()
        binding.apply {
            editExpenseAmount.setText(expense.amount.toString())
            editEtDescription.setText(expense.description.orEmpty())
            editDetailExpenseDate.text = formatDate(expenseTimestamp.toDate())
            setUpExpenseDirectionUi(expense.direction)
        }
    }
    private fun setUpExpenseDirectionUi(direction: ExpenseDirection) {
        binding.apply {
            when (direction) {
                ExpenseDirection.DIYE -> {
                    editExpenseToolbar.title = getString(R.string.maine_diye_3)
                    setExpenseSaveButtonColor(R.color.red)
                }
                ExpenseDirection.LIYE -> {
                    editExpenseToolbar.title = getString(R.string.maine_liye)
                    setExpenseSaveButtonColor(R.color.green)
                }
            }
        }
    }
    private fun setExpenseSaveButtonColor(color: Int) {
        binding.editExpenseSave.backgroundTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), color)
            )
    }
    private fun setUpClicks() {
        binding.apply {
            editExpenseToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            editDetailExpenseDateCard.setOnClickListener {
                showPastDatePicker { selectedDate ->
                    expenseTimestamp = Timestamp(selectedDate)
                    editDetailExpenseDate.text = formatDate(selectedDate)
                }
            }
            editExpenseSave.setOnClickListener {
                val amount = editExpenseAmount.text.toString().toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    editExpenseAmount.error = "Enter a valid amount"
                    return@setOnClickListener
                }
                val updatedExpense = args.expense.copy(
                    amount = amount,
                    description = editEtDescription.text.toString().trim(),
                    date = expenseTimestamp
                )
                expenseViewModel.updateExpense(
                    businessId = businessViewModel.businessId.value.orEmpty(),
                    expense = updatedExpense
                )
            }
        }
    }
    private fun observeUpdateExpenseState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.updateExpenseState.collect { state ->
                    when (state) {
                        is UiState.Idle -> hideProgress()
                        is UiState.Loading -> showProgress()
                        is UiState.Success -> {
                            hideProgress()
                            expenseViewModel.resetUpdateExpenseState()
                            findNavController().navigateUp()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            expenseViewModel.resetUpdateExpenseState()
                        }
                    }
                }
            }
        }
    }
    private fun showProgress() {
        binding.editExpenseProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.editExpenseProgressOverlay.visibility = View.GONE
    }
}
