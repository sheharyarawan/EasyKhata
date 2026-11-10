package com.example.easykhata.ui.fragments.expense
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.easykhata.R
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.databinding.FragmentExpenseDetailBinding
import com.example.easykhata.utils.formatDate
import com.example.easykhata.utils.formatParchaDate
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
@AndroidEntryPoint
class ExpenseDetailFragment : Fragment(R.layout.fragment_expense_detail) {
    private lateinit var binding: FragmentExpenseDetailBinding
    private val args: ExpenseDetailFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExpenseDetailBinding.bind(view)
        setUpUi()
        setUpClicks()
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
                    else -> false
                }
            }
        }
    }
}
