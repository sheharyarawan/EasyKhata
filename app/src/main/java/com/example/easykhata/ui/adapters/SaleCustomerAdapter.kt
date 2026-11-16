package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.data.models.customers.CustomerWithBalance
import com.example.easykhata.databinding.ItemSaleCustomerBinding
class SaleCustomerAdapter(
    private val onCustomerClick: (Customer) -> Unit
) : ListAdapter<CustomerWithBalance, SaleCustomerAdapter.CustomerViewHolder>(
    CustomerDiffCallback()
) {
    inner class CustomerViewHolder(
        private val binding: ItemSaleCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(customerWithBalance: CustomerWithBalance) {
            val customer = customerWithBalance.customer
            binding.apply {
                customerName.text = customer.name
                customerPhone.text = customer.phone
                root.setOnClickListener {
                    onCustomerClick(customer)
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerViewHolder {
        val binding = ItemSaleCustomerBinding.inflate(
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
    class CustomerDiffCallback :
        DiffUtil.ItemCallback<CustomerWithBalance>() {
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