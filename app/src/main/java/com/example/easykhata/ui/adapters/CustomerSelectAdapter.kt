package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.databinding.ItemCustomerSelectKareinBinding
class CustomerSelectAdapter(
    private val onCustomerClick: (CustomerWithBalance) -> Unit
) : ListAdapter<CustomerWithBalance, CustomerSelectAdapter.CustomerViewHolder>(
    CustomerDiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerViewHolder {
        val binding = ItemCustomerSelectKareinBinding.inflate(
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
    inner class CustomerViewHolder(
        private val binding: ItemCustomerSelectKareinBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerWithBalance) {
            val customer = item.customer
            binding.customerName.text = customer.name
            binding.customerInitials.text = customer.name
                .trim()
                .split("\\s+".toRegex())
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
            binding.root.setOnClickListener {
                onCustomerClick(item)
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