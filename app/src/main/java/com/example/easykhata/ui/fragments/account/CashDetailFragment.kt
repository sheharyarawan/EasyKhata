package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentCashDetailBinding

class CashDetailFragment : Fragment(R.layout.fragment_cash_detail) {
    lateinit var binding: FragmentCashDetailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentCashDetailBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            cashDetailToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            cashEditButton.setOnClickListener {
                findNavController().navigate(R.id.action_cashDetailFragment_to_addExpenseFragment)
            }
        }
    }
}