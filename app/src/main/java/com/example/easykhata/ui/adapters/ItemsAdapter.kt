package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.databinding.ItemMereItemsBinding
import java.text.DecimalFormat
class ItemsAdapter(
    private val onItemClick: (SaleItem) -> Unit,
    private val onMenuClick: (SaleItem, View) -> Unit
) : ListAdapter<SaleItem, ItemsAdapter.ItemViewHolder>(ItemDiffCallback()) {
    inner class ItemViewHolder(
        private val binding: ItemMereItemsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SaleItem) {
            binding.apply {
                itemType.text = item.name
                itemPrice.text = buildString {
        append("Rs ")
        append(DecimalFormat("#,##0.##").format(item.price))
    }
                categoryUnit.text = buildString {
        append("per ")
        append(item.unitSymbol)
    }
                root.setOnClickListener {
                    onItemClick(item)
                }
                itemMenu.setOnClickListener {
                    onMenuClick(item, itemMenu)
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val binding = ItemMereItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ItemDiffCallback : DiffUtil.ItemCallback<SaleItem>() {
        override fun areItemsTheSame(oldItem: SaleItem, newItem: SaleItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SaleItem, newItem: SaleItem) =
            oldItem == newItem
    }
}