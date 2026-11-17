package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.sales.Sale
import com.example.easykhata.databinding.ItemSalesBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt
class SalesAdapter(
    private val onSaleClick: (Sale) -> Unit,
    private val onSaleMenuClick: (Sale, View) -> Unit
) : ListAdapter<Sale, SalesAdapter.SaleViewHolder>(SaleDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SaleViewHolder {
        val binding = ItemSalesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: SaleViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    inner class SaleViewHolder(
        private val binding: ItemSalesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sale: Sale) = with(binding) {
            val customer = sale.customerName.trim()
            if (customer.isBlank()) {
                customerName.text = buildString {
        append("Walk-in customer")
    }
                customerInitials.visibility = View.GONE
                customerIcon.visibility = View.VISIBLE
            } else {
                customerName.text = customer
                customerInitials.text = getInitials(customer)
                customerInitials.visibility = View.VISIBLE
                customerIcon.visibility = View.GONE
            }
            invoiceNumber.text = if (
                sale.invoiceNumber.startsWith("INV-", ignoreCase = true)
            ) {
                sale.invoiceNumber
            } else {
                "INV-${sale.invoiceNumber}"
            }
            invoiceDateTime.text = formatDate(sale.date)
            saleAmount.text = buildString {
        append("Rs. ")
        append(String.format(Locale.getDefault(), "%.0f", sale.totalPrice))
    }
            when (sale.paymentMethod.name.uppercase(Locale.ROOT)) {
                "NAQAD" -> {
                    paymentType.text = buildString {
        append("NAQAD")
    }
                    paymentType.setTextColor("#2E7D32".toColorInt())
                    paymentTypeCard.setCardBackgroundColor(
                        "#E8F5E9".toColorInt()
                    )
                }
                "UDHAAR" -> {
                    paymentType.text = buildString {
        append("UDHAAR")
    }
                    paymentType.setTextColor("#EF6C00".toColorInt())
                    paymentTypeCard.setCardBackgroundColor(
                        "#FFF3E0".toColorInt()
                    )
                }
                else -> {
                    paymentType.text = sale.paymentMethod.name
                }
            }
            root.setOnClickListener {
                onSaleClick(sale)
            }
            saleMenu.setOnClickListener {
                onSaleMenuClick(sale, it)
            }
        }
    }
    private fun getInitials(name: String): String {
        return name.trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
    }
    private fun formatDate(timestamp: Long): String {
        if (timestamp <= 0L) return ""
        return SimpleDateFormat(
            "dd MMM, h:mm a",
            Locale.getDefault()
        ).format(Date(timestamp))
    }
    class SaleDiffCallback : DiffUtil.ItemCallback<Sale>() {
        override fun areItemsTheSame(
            oldItem: Sale,
            newItem: Sale
        ): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(
            oldItem: Sale,
            newItem: Sale
        ): Boolean {
            return oldItem == newItem
        }
    }
}
