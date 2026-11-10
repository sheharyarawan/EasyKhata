package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.R
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.expense.ExpenseWithBalance
import com.example.easykhata.databinding.ItemExpenseBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
class ExpenseAdapter :
    ListAdapter<ExpenseWithBalance, ExpenseAdapter.ExpenseViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: ExpenseViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    inner class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExpenseWithBalance) {
            val expense = item.expense
            binding.apply {
                val date = expense.date?.toDate()
                if (date != null) {
                    tvTransactionDate.text = formatDate(date)
                    tvTransactionTime.text = formatTime(date)
                } else {
                    tvTransactionDate.setText(R.string.date_unavailable)
                    tvTransactionTime.text = ""
                }
                val amount = "Rs. ${formatAmount(expense.amount)}"
                when (expense.direction) {
                    ExpenseDirection.DIYE -> {
                        tvTransactionIcon.apply {
                            setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_arrow_upward, 0, 0, 0
                            )
                            background = ContextCompat.getDrawable(
                                root.context,
                                R.drawable.bg_red
                            )
                            TextViewCompat.setCompoundDrawableTintList(
                                tvBalanceLabel,
                                ContextCompat.getColorStateList(
                                    root.context,
                                    R.color.red
                                )
                            )
                        }
                        tvDiye.text = amount
                        tvLiye.text = ""
                    }
                    ExpenseDirection.LIYE -> {
                        tvTransactionIcon.apply {
                            setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_arrow_downward, 0, 0, 0
                            )
                            background = ContextCompat.getDrawable(
                                root.context,
                                R.drawable.bg_green
                            )
                            TextViewCompat.setCompoundDrawableTintList(
                                tvBalanceLabel,
                                ContextCompat.getColorStateList(
                                    root.context,
                                    R.color.green
                                )
                            )
                        }
                        tvDiye.text = ""
                        tvLiye.text = amount
                    }
                }
                when {
                    item.runningBalance > 0 -> {
                        tvBalanceLabel.visibility = View.VISIBLE
                        tvBalanceLabel.text = root.context.getString(
                            R.string.amount_rupees,
                            formatAmount(item.runningBalance)
                        )

                    }
                    item.runningBalance < 0 -> {
                        tvBalanceLabel.visibility = View.VISIBLE
                        tvBalanceLabel.text = root.context.getString(
                            R.string.amount_rupees,
                            formatAmount(abs(item.runningBalance))
                        )
                    }
                    else -> {
                        tvBalanceLabel.visibility = View.GONE
                    }
                }
                if (expense.description.isNotBlank()) {
                    tvTransactionDescription.visibility = View.VISIBLE
                    tvTransactionDescription.text = expense.description
                } else {
                    tvTransactionDescription.visibility = View.GONE
                }
            }
        }
    }
    private fun formatDate(date: Date): String {
        return SimpleDateFormat(
            "d MMM",
            Locale.getDefault()
        ).format(date)
    }
    private fun formatTime(date: Date): String {
        return SimpleDateFormat(
            "h:mm a",
            Locale.getDefault()
        ).format(date)
    }
    private fun formatAmount(amount: Double): String {
        return DecimalFormat("#,##0.##").format(amount)
    }
    class DiffCallback : DiffUtil.ItemCallback<ExpenseWithBalance>() {
        override fun areItemsTheSame(
            oldItem: ExpenseWithBalance,
            newItem: ExpenseWithBalance
        ): Boolean {
            return oldItem.expense.id == newItem.expense.id
        }
        override fun areContentsTheSame(
            oldItem: ExpenseWithBalance,
            newItem: ExpenseWithBalance
        ): Boolean {
            return oldItem == newItem
        }
    }
}
