package com.example.easykhata.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.customers.Contact
import com.example.easykhata.databinding.ItemAddCustomerBinding
class PhoneContactsAdapter :
    ListAdapter<Contact, PhoneContactsAdapter.ContactViewHolder>(
        ContactDiffCallback()
    ) {
     class ContactViewHolder(
        private val binding: ItemAddCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.itemSearchCustomerInitials.text =
                contact.name.firstOrNull()?.uppercase() ?: "?"
            binding.itemAddCustomerName.text = contact.name
            binding.itemAddCustomerNumber.text = contact.phoneNumber
            binding.itemAddCustomerAdded.visibility = View.GONE
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
    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(
            oldItem: Contact,
            newItem: Contact
        ): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.phoneNumber == newItem.phoneNumber
        }
        override fun areContentsTheSame(
            oldItem: Contact,
            newItem: Contact
        ): Boolean {
            return oldItem == newItem
        }
    }
}