package com.example.easykhata.ui.fragments.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentSaleConfirmKareinBinding

class SaleConfirmKareinFragment : Fragment(R.layout.fragment_sale_confirm_karein) {
lateinit var binding: FragmentSaleConfirmKareinBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentSaleConfirmKareinBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            saleConfirmToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}