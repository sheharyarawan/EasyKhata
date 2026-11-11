package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.R
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.databinding.ItemSearchCustomerBinding
import java.util.Locale
import kotlin.math.abs
class SearchCustomerAdapter(
    private val onCustomerClick: (String) -> Unit
) : ListAdapter<CustomerWithBalance, SearchCustomerAdapter.SearchCustomerViewHolder>(
    CustomerDiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchCustomerViewHolder {
        val binding = ItemSearchCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchCustomerViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: SearchCustomerViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    inner class SearchCustomerViewHolder(
        private val binding: ItemSearchCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerWithBalance) {
            binding.apply {
                itemSearchCustomerName.text = item.customer.name
                itemSearchCustomerInitials.text = item.customer.name
                    .trim()
                    .split("\\s+".toRegex())
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                    .joinToString("")
                itemSearchCustomerAmount.apply {
                    text = String.format(
                        Locale.getDefault(),
                        "Rs %,.0f",
                        abs(item.balance)
                    )
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            when {
                                item.balance > 0 -> R.color.green
                                item.balance < 0 -> R.color.red
                                else -> R.color.green
                            }
                        )
                    )
                }
                root.setOnClickListener {
                    onCustomerClick(item.customer.id)
                }
            }
        }
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