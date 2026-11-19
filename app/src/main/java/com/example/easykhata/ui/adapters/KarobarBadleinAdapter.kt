package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.R
import com.example.easykhata.data.models.auth.BusinessUi
import com.example.easykhata.databinding.ItemKarobarBadleinBinding
class KarobarBadleinAdapter(
    private val onBusinessClick: (BusinessUi) -> Unit,
    private val onDeleteClick: (BusinessUi) -> Unit
) : ListAdapter<BusinessUi, KarobarBadleinAdapter.BusinessViewHolder>(
    BusinessDiffCallback()
) {
    inner class BusinessViewHolder(
        private val binding: ItemKarobarBadleinBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BusinessUi) {
            binding.businessName.text = item.business.businessName
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (item.isActive) R.color.ltt_gray
                    else android.R.color.transparent
                )
            )
            binding.businessDelete.visibility =
                if (item.isActive) View.GONE else View.VISIBLE
            binding.root.setOnClickListener {
                if (!item.isActive) {
                    onBusinessClick(item)
                }
            }
            binding.businessDelete.setOnClickListener {
                if (!item.isActive) {
                    onDeleteClick(item)
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BusinessViewHolder {
        val binding = ItemKarobarBadleinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BusinessViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: BusinessViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    class BusinessDiffCallback : DiffUtil.ItemCallback<BusinessUi>() {
        override fun areItemsTheSame(
            oldItem: BusinessUi,
            newItem: BusinessUi
        ): Boolean {
            return oldItem.business.businessId ==
                    newItem.business.businessId
        }
        override fun areContentsTheSame(
            oldItem: BusinessUi,
            newItem: BusinessUi
        ): Boolean {
            return oldItem == newItem
        }
    }
}