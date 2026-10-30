package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentCashbookBinding

class CashbookFragment : Fragment(R.layout.fragment_cashbook) {
    lateinit var binding: FragmentCashbookBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentCashbookBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            cashBookToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            cashInButton.setOnClickListener {
                findNavController().navigate(R.id.action_cashbookFragment_to_cashDetailFragment)
            }
            cashOutButton.setOnClickListener {
                findNavController().navigate(R.id.action_cashbookFragment_to_cashDetailFragment)
            }
            puranaRecordCard.setOnClickListener {
                findNavController().navigate(R.id.action_cashbookFragment_to_puranaRecordFragment)
            }
        }
    }
}