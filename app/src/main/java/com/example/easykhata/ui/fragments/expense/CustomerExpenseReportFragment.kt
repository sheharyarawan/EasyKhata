package com.example.easykhata.ui.fragments.expense

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentCustomerExpenseReportBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class CustomerExpenseReportFragment : Fragment(R.layout.fragment_customer_expense_report) {
    lateinit var binding: FragmentCustomerExpenseReportBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentCustomerExpenseReportBinding.bind(view)
    }
}