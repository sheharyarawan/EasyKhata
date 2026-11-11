package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.R
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.databinding.ItemCustomersBinding
import java.text.SimpleDateFormat
import java.util.Locale
class CustomerAdapter(
    private val onCustomerClick: (String) -> Unit
) : ListAdapter<CustomerWithBalance, CustomerAdapter.CustomerViewHolder>(
    CustomerDiffCallback()
) {
    inner class CustomerViewHolder(
        private val binding: ItemCustomersBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerWithBalance) {
            val customer = item.customer
            val initials = customer.name
                .trim()
                .split("\\s+".toRegex())
                .filter { it.isNotEmpty() }
                .take(2)
                .joinToString("") { it.first().uppercase() }
            binding.tvInitials.text = initials.ifEmpty { "?" }
            binding.tvCustomerName.text = customer.name
            binding.tvLastTransaction.text =
                item.lastTransaction?.toDate()?.let {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(it)
                } ?: "No transactions yet"
            binding.tvAmount.text = when {
                item.balance > 0 -> String.format(
                    Locale.getDefault(), "Rs %,.0f", item.balance
                )
                item.balance < 0 -> String.format(
                    Locale.getDefault(), "Rs %,.0f", -item.balance
                )
                else -> "Rs 0"
            }
            val amountColor = when {
                item.balance > 0 -> R.color.red
                item.balance < 0 -> R.color.green
                else -> R.color.green
            }
            binding.tvAmount.setTextColor(
                ContextCompat.getColor(binding.root.context, amountColor)
            )
            binding.root.setOnClickListener {
                onCustomerClick(customer.id)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerViewHolder {
        val binding = ItemCustomersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomerViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: CustomerViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    class CustomerDiffCallback : DiffUtil.ItemCallback<CustomerWithBalance>() {
        override fun areItemsTheSame(
            oldItem: CustomerWithBalance,
            newItem: CustomerWithBalance
        ): Boolean {
            return oldItem.customer.id == newItem.customer.id
        }
        override fun areContentsTheSame(
            oldItem: CustomerWithBalance,
            newItem: CustomerWithBalance
        ): Boolean {
            return oldItem == newItem
        }
    }
}
