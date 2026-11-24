package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.account.VisitingCardUiState
import com.example.easykhata.databinding.ItemVisitingCardBinding
class VisitingCardAdapter :
    ListAdapter<VisitingCardUiState, VisitingCardAdapter.VisitingCardViewHolder>(
        VisitingCardDiffCallback()
    ) {
     class VisitingCardViewHolder(
        private val binding: ItemVisitingCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VisitingCardUiState) {
            binding.apply {
                businessName.text = item.businessName
                businessType.text = item.businessType
                tvName.text = item.ownerName
                tvMobileNumber.text = item.phoneNumber
                tvAddress.text = item.address
                visitingCardLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        item.backgroundColor
                    )
                )
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VisitingCardViewHolder {
        val binding = ItemVisitingCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        binding.root.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return VisitingCardViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: VisitingCardViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    class VisitingCardDiffCallback :
        DiffUtil.ItemCallback<VisitingCardUiState>() {
        override fun areItemsTheSame(
            oldItem: VisitingCardUiState,
            newItem: VisitingCardUiState
        ): Boolean {
            return oldItem.backgroundColor == newItem.backgroundColor
        }
        override fun areContentsTheSame(
            oldItem: VisitingCardUiState,
            newItem: VisitingCardUiState
        ): Boolean {
            return oldItem == newItem
        }
    }
}
