package com.example.easykhata.ui.fragments.expense

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentAddExpenseBinding
class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {
    lateinit var binding: FragmentAddExpenseBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentAddExpenseBinding.bind(view)
    }
}