package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.sales.SelectedSaleItem
import com.example.easykhata.databinding.ItemSaleConfirmBinding
import java.text.DecimalFormat
class SaleConfirmAdapter :
    ListAdapter<SelectedSaleItem, SaleConfirmAdapter.SaleConfirmViewHolder>(
        SaleConfirmDiffCallback()
    ) {
    private val decimalFormat = DecimalFormat("#,##0.##")
    inner class SaleConfirmViewHolder(
        private val binding: ItemSaleConfirmBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(selectedSaleItem: SelectedSaleItem) {
            val item = selectedSaleItem.item
            val quantity = selectedSaleItem.quantity
            val lineTotal = item.price * quantity
            binding.apply {
                itemName.text = item.name
                itemQuantity.text = buildString {
        append(quantity)
        append(" ")
        append(item.unitSymbol)
    }
                itemPrice.text = buildString {
        append("Rs ")
        append(decimalFormat.format(item.price))
    }
                itemTotalPrice.text = buildString {
        append("Rs ")
        append(decimalFormat.format(lineTotal))
    }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SaleConfirmViewHolder {
        val binding = ItemSaleConfirmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleConfirmViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: SaleConfirmViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    class SaleConfirmDiffCallback :
        DiffUtil.ItemCallback<SelectedSaleItem>() {
        override fun areItemsTheSame(
            oldItem: SelectedSaleItem,
            newItem: SelectedSaleItem
        ): Boolean {
            return oldItem.item.id == newItem.item.id
        }
        override fun areContentsTheSame(
            oldItem: SelectedSaleItem,
            newItem: SelectedSaleItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
