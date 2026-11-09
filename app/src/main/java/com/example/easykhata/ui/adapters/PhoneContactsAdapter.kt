package com.example.easykhata.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.customers.Contact
import com.example.easykhata.data.models.customers.PhoneContactUiModel
import com.example.easykhata.databinding.ItemAddCustomerBinding
import com.google.android.material.snackbar.Snackbar
import androidx.core.graphics.toColorInt
class PhoneContactsAdapter(
    private val onAddClick: (Contact) -> Unit
) : ListAdapter<PhoneContactUiModel, PhoneContactsAdapter.ContactViewHolder>(
    ContactDiffCallback()
) {
    inner class ContactViewHolder(
        private val binding: ItemAddCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PhoneContactUiModel) {
            val contact = item.contact
            val isAdded = item.isAdded
            binding.apply {
                itemSearchCustomerInitials.text =
                    contact.name.firstOrNull()?.uppercase() ?: "?"
                itemAddCustomerName.text = contact.name
                itemAddCustomerNumber.text = contact.phoneNumber
                itemAddCustomerAdded.apply {
                    visibility = View.VISIBLE
                    text = if (isAdded) "ADDED" else "+ ADD"
                    setTextColor(
                        if (isAdded) {
                            Color.GRAY
                        } else {
                            "#3F51B5".toColorInt()
                        }
                    )
                    alpha = if (isAdded) 0.5f else 1f
                }
                val handleClick = {
                    if (isAdded) {
                        Snackbar.make(
                            root,
                            "Yeh person already added hai",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        onAddClick(contact)
                    }
                }
                root.setOnClickListener {
                    handleClick()
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactViewHolder {
        val binding = ItemAddCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    class ContactDiffCallback :
        DiffUtil.ItemCallback<PhoneContactUiModel>() {
        override fun areItemsTheSame(
            oldItem: PhoneContactUiModel,
            newItem: PhoneContactUiModel
        ): Boolean {
            return oldItem.contact.id == newItem.contact.id &&
                    oldItem.contact.phoneNumber == newItem.contact.phoneNumber
        }
        override fun areContentsTheSame(
            oldItem: PhoneContactUiModel,
            newItem: PhoneContactUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}