package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.data.models.sales.SelectedSaleItem
import com.example.easykhata.databinding.ItemCatalogBinding
import java.text.DecimalFormat
class SaleCatalogAdapter(
    private val onAddClick: (SaleItem) -> Unit,
    private val onRemoveClick: (SaleItem) -> Unit
) : ListAdapter<SelectedSaleItem, SaleCatalogAdapter.SaleItemViewHolder>(
    ItemDiffCallback()
) {
    inner class SaleItemViewHolder(
        private val binding: ItemCatalogBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(selectedItem: SelectedSaleItem) {
            val item = selectedItem.item
            binding.apply {
                itemType.text = item.name
                itemPrice.text =
                    buildString {
        append("Rs ")
        append(DecimalFormat("#,##0.##").format(item.price))
    }
                categoryUnit.text = buildString {
        append("per ")
        append(item.unitSymbol)
    }
                itemCount.text = selectedItem.quantity.toString()
                addItem.setOnClickListener {
                    onAddClick(item)
                }
                removeItem.setOnClickListener {
                    onRemoveClick(item)
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SaleItemViewHolder {
        val binding = ItemCatalogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleItemViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: SaleItemViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    class ItemDiffCallback : DiffUtil.ItemCallback<SelectedSaleItem>() {
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