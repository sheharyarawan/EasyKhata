package com.example.easykhata.ui.adapters
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.account.WasooliCustomerItem
import com.example.easykhata.databinding.ItemFragmentAnayWaleBinding
import java.text.SimpleDateFormat
import java.util.Locale
class WasooliAdapter(
    private val onReminderClick: (WasooliCustomerItem) -> Unit
) : ListAdapter<WasooliCustomerItem, WasooliAdapter.WasooliViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WasooliViewHolder {
        val binding = ItemFragmentAnayWaleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WasooliViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: WasooliViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    inner class WasooliViewHolder(
        private val binding: ItemFragmentAnayWaleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WasooliCustomerItem) {
            val customer = item.customerWithBalance.customer
            val balance = item.customerWithBalance.balance
            binding.customerName.text = customer.name
            binding.customerInitials.text = customer.name
                .trim()
                .split("\\s+".toRegex())
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
            binding.customerAmount.text = "Rs. ${balance.toInt()}"

            binding.customerDate.text = item.reminder.reminderDate?.let {
                SimpleDateFormat("dd MMM", Locale.getDefault())
                    .format(it.toDate())
            } ?: ""
            binding.root.setOnClickListener {
                onReminderClick(item)
            }
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<WasooliCustomerItem>() {
        override fun areItemsTheSame(
            oldItem: WasooliCustomerItem,
            newItem: WasooliCustomerItem
        ): Boolean {
            return oldItem.reminder.customerId ==
                    newItem.reminder.customerId
        }
        override fun areContentsTheSame(
            oldItem: WasooliCustomerItem,
            newItem: WasooliCustomerItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
