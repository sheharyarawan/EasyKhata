package com.example.easykhata.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.databinding.ItemKarobarTypeBinding
class KarobarTypeAdapter :
    ListAdapter<String, KarobarTypeAdapter.KarobarTypeViewHolder>(DiffCallback()) {
    private var selectedType: String? = null
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
            karobarName.text = type
            icCheckKarobarType.visibility =
                if (selectedType == type) View.VISIBLE else View.GONE
            root.setOnClickListener {
                val previousType = selectedType
                selectedType = type
                val previousPosition = currentList.indexOf(previousType)
                val newPosition = currentList.indexOf(type)
                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition)
                }
                if (newPosition != -1 && newPosition != previousPosition) {
                    notifyItemChanged(newPosition)
                }
            }
        }
    }
    fun getSelectedType(): String? = selectedType
    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}