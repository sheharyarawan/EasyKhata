package com.example.easykhata.ui.fragments.customers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentHomeBinding
import com.example.easykhata.ui.fragments.customers.bottomSheets.CustomerFilterBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
lateinit var binding: FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentHomeBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            searchCardView.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchCustomerFragment)
            }
            etSearchCustomerExpand.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchCustomerFragment)
            }
            addCustomerButton.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_addCustomerFragment)
            }
            cardPdf.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_khataReportFragment)
            }
            cardSort.setOnClickListener {
                CustomerFilterBottomSheet().show(
                    parentFragmentManager,
                    "CustomerFilterBottomSheet"
                )
            }
        }
    }
}