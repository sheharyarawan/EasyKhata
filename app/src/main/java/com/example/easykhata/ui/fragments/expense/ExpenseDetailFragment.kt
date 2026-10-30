package com.example.easykhata.ui.fragments.expense

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentExpenseDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseDetailFragment : Fragment(R.layout.fragment_expense_detail) {
lateinit var binding: FragmentExpenseDetailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentExpenseDetailBinding.bind(view)
    }
}