package com.example.easykhata.ui.fragments.expense
import android.os.Bundle
import android.view.View
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
import com.example.easykhata.databinding.FragmentExpenseDetailBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.expense.ExpenseViewModel
import com.example.easykhata.utils.formatDate
import com.example.easykhata.utils.formatParchaDate
import com.example.easykhata.utils.showConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
@AndroidEntryPoint
class ExpenseDetailFragment : Fragment(R.layout.fragment_expense_detail) {
    private lateinit var binding: FragmentExpenseDetailBinding
    private val args: ExpenseDetailFragmentArgs by navArgs()
    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExpenseDetailBinding.bind(view)
        setUpUi()
        setUpClicks()
        observeDeleteExpenseState()
    }
    private fun setUpUi() {
        val expense = args.expense
        binding.apply {
            val amount = DecimalFormat("#,##0.##").format(expense.amount)
            detailExpenseAmount.text = amount
            parchaAmount.text = getString(R.string.amount_rupees, amount)
            val date = expense.date?.toDate()
            if (date != null) {
                detailExpenseDate.text = formatDate(date)
                parchaDate.text = formatParchaDate(date)
            } else {
                detailExpenseDate.setText(R.string.date_unavailable)
                parchaDate.setText(R.string.date_unavailable)
            }
            val hasDescription = expense.description.isNotBlank()
            editCustomerSupplier.visibility =
                if (hasDescription) View.VISIBLE else View.GONE
            parchaTafseelTv.visibility =
                if (hasDescription) View.VISIBLE else View.GONE
            parchaDescription.visibility =
                if (hasDescription) View.VISIBLE else View.GONE
            if (hasDescription) {
                detailExpenseTv.text = expense.description
                parchaDescription.text = expense.description
            }
            when (expense.direction) {
                ExpenseDirection.DIYE -> setupDiyeUi()
                ExpenseDirection.LIYE -> setUpLiyeUi()
            }
        }
    }
    private fun setupDiyeUi() {
        binding.apply {
            expenseDetailToolbar.title = getString(R.string.maine_diye_3)
            parchaDirection.setText(R.string.maine_diye_3)
            diyeLiyeColorCard.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.green)
            )
        }
    }
    private fun setUpLiyeUi() {
        binding.apply {
            expenseDetailToolbar.title = getString(R.string.maine_liye)
            parchaDirection.setText(R.string.maine_liye)
            diyeLiyeColorCard.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.red)
            )
        }
    }
    private fun setUpClicks() {
        binding.apply {
            val navigateToEdit= View.OnClickListener{
                val action= ExpenseDetailFragmentDirections.actionExpenseDetailFragmentToEditExpenseFragment(
                    args.expense
                )
                findNavController().navigate(action)
            }
            detailExpenseCardView.setOnClickListener(navigateToEdit)
            detailExpenseDateCard.setOnClickListener(navigateToEdit)
            editCustomerSupplier.setOnClickListener(navigateToEdit)
            editExpenseButton.setOnClickListener(navigateToEdit)
            expenseDetailToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            expenseDetailToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.deleteExpense -> {
                        showConfirmationDialog(
                            title = "Entry delete karein?",
                            message = "Kya aap entry delete karna chahte hain?"
                        ) {
                            deleteExpense()
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
    private fun deleteExpense() {
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Business not selected", Toast.LENGTH_SHORT).show()
            return
        }
        expenseViewModel.deleteExpense(
            businessId = businessId,
            expenseId = args.expense.id
        )
    }
    private fun observeDeleteExpenseState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.deleteExpenseState.collect { state ->
                    when (state) {
                        is UiState.Idle -> hideProgress()
                        is UiState.Loading -> showProgress()
                        is UiState.Success -> {
                            hideProgress()
                            expenseViewModel.resetDeleteExpenseState()
                            findNavController().navigateUp()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            expenseViewModel.resetDeleteExpenseState()
                        }
                    }
                }
            }
        }
    }
    private fun showProgress() {
        binding.expenseProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.expenseProgressOverlay.visibility = View.GONE
    }
}
