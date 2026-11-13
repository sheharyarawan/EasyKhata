package com.example.easykhata.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.customers.KhataReportItem
import com.example.easykhata.databinding.ItemKhataReportBinding
import java.text.SimpleDateFormat
import java.util.Locale
class KhataReportAdapter :
    ListAdapter<KhataReportItem, KhataReportAdapter.KhataReportViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KhataReportViewHolder {
        val binding = ItemKhataReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KhataReportViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: KhataReportViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
     class KhataReportViewHolder(
        private val binding: ItemKhataReportBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KhataReportItem) = with(binding) {
            val expense = item.expense
            tvName.text = item.customerName
            tvDate.text = expense.date?.toDate()?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
            } ?: ""
            tvDiyeAmount.text =
                if (expense.direction == ExpenseDirection.DIYE)
                    "Rs ${"%,.0f".format(expense.amount)}"
                else ""
            tvLiyeAmount.text =
                if (expense.direction == ExpenseDirection.LIYE)
                    "Rs ${"%,.0f".format(expense.amount)}"
                else ""
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<KhataReportItem>() {
        override fun areItemsTheSame(
            oldItem: KhataReportItem,
            newItem: KhataReportItem
        ) = oldItem.expense.id == newItem.expense.id

        override fun areContentsTheSame(
            oldItem: KhataReportItem,
            newItem: KhataReportItem
        ) = oldItem == newItem
    }
}