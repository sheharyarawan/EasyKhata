package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.sales.UnitItem
import com.example.easykhata.databinding.ItemUnitChuneinBinding
class UnitAdapter(
    private val onUnitSelected: (UnitItem) -> Unit
) : ListAdapter<UnitItem, UnitAdapter.UnitViewHolder>(UnitDiffCallback()) {
    inner class UnitViewHolder(
        private val binding: ItemUnitChuneinBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(unit: UnitItem) {
            binding.unitType.text = unit.name
            binding.unitSymbol.text = unit.symbol
            binding.unitCheck.visibility =
                if (unit.isSelected) View.VISIBLE else View.GONE
            binding.root.setOnClickListener {
                onUnitSelected(unit)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UnitViewHolder {
        val binding = ItemUnitChuneinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UnitViewHolder(binding)
    }
    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class UnitDiffCallback : DiffUtil.ItemCallback<UnitItem>() {
        override fun areItemsTheSame(
            oldItem: UnitItem,
            newItem: UnitItem
        ): Boolean {
            return oldItem.name == newItem.name
        }
        override fun areContentsTheSame(
            oldItem: UnitItem,
            newItem: UnitItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}