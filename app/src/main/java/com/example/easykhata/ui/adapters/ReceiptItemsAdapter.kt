package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.sales.SaleItemDetail
import com.example.easykhata.databinding.ItemSaleReciptBinding
import java.text.DecimalFormat
class ReceiptItemsAdapter :
    ListAdapter<SaleItemDetail, ReceiptItemsAdapter.ReceiptItemViewHolder>(
        DiffCallback()
    ) {
    private val decimalFormat = DecimalFormat("#,##0.##")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReceiptItemViewHolder {
        val binding = ItemSaleReciptBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReceiptItemViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: ReceiptItemViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    inner class ReceiptItemViewHolder(
        private val binding: ItemSaleReciptBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SaleItemDetail) {
            binding.itemName.text = item.itemName
            val quantity = decimalFormat.format(item.quantity)
            val unit = item.unitSymbol.ifBlank { item.unit }
            binding.itemQuantity.text = buildString {
                append(quantity)
                if (unit.isNotBlank()) {
                    append(" ")
                    append(unit)
                }
            }
            binding.itemAmount.text =
                buildString {
        append("Rs ")
        append(decimalFormat.format(item.total))
    }
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<SaleItemDetail>() {
        override fun areItemsTheSame(
            oldItem: SaleItemDetail,
            newItem: SaleItemDetail
        ): Boolean {
            return oldItem.itemId == newItem.itemId
        }
        override fun areContentsTheSame(
            oldItem: SaleItemDetail,
            newItem: SaleItemDetail
        ): Boolean {
            return oldItem == newItem
        }
    }
}
