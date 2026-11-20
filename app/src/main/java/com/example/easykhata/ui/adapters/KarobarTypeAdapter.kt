package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.account.KarobarType
import com.example.easykhata.databinding.ItemKarobarTypeBinding
import kotlin.collections.map
class KarobarTypeAdapter :
    ListAdapter<KarobarType, KarobarTypeAdapter.KarobarTypeViewHolder>(DiffCallback()) {
    class KarobarTypeViewHolder(
        val binding: ItemKarobarTypeBinding
    ) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KarobarTypeViewHolder {
        val binding = ItemKarobarTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KarobarTypeViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: KarobarTypeViewHolder,
        position: Int
    ) {
        val type = getItem(position)
        holder.binding.apply {
            karobarName.text = type.name
            icCheckKarobarType.visibility =
                if (type.isSelected) View.VISIBLE else View.GONE
            root.setOnClickListener {
                val updatedList = currentList.map {
                    it.copy(isSelected = it.name == type.name)
                }
                submitList(updatedList)
            }
        }
    }
    fun getSelectedType(): String? {
        return currentList.find { it.isSelected }?.name
    }
    fun setSelectedType(type: String?) {
        submitList(
            currentList.map {
                it.copy(isSelected = it.name == type)
            }
        )
    }
    class DiffCallback : DiffUtil.ItemCallback<KarobarType>() {
        override fun areItemsTheSame(
            oldItem: KarobarType,
            newItem: KarobarType
        ): Boolean {
            return oldItem.name == newItem.name
        }
        override fun areContentsTheSame(
            oldItem: KarobarType,
            newItem: KarobarType
        ): Boolean {
            return oldItem == newItem
        }
    }
}