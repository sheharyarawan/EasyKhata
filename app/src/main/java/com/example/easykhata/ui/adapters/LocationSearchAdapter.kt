package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.account.LocationSearchResult
import com.example.easykhata.databinding.ItemLocationSearchResultBinding
class LocationSearchAdapter(
    private val onLocationClick: (LocationSearchResult) -> Unit
) : ListAdapter<LocationSearchResult, LocationSearchAdapter.LocationViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(binding)
    }
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class LocationViewHolder(
        private val binding: ItemLocationSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(location: LocationSearchResult) {
            binding.apply {
                tvLocationName.text = location.name
                tvLocationAddress.text = location.address
                root.setOnClickListener {
                    onLocationClick(location)
                }
            }
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<LocationSearchResult>() {
        override fun areItemsTheSame(
            oldItem: LocationSearchResult,
            newItem: LocationSearchResult
        ): Boolean {
            return oldItem.latitude == newItem.latitude &&
                    oldItem.longitude == newItem.longitude
        }
        override fun areContentsTheSame(
            oldItem: LocationSearchResult,
            newItem: LocationSearchResult
        ): Boolean {
            return oldItem == newItem
        }
    }
}