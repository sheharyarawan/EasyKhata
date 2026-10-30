package com.example.easykhata.ui.fragments.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentSaleDarjkareinBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaleDarjkareinFragment : Fragment(R.layout.fragment_sale_darjkarein) {
    lateinit var binding: FragmentSaleDarjkareinBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentSaleDarjkareinBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            saleDarjKareinToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            nayaItemAddButtonSale.setOnClickListener {
                findNavController().navigate(R.id.action_saleDarjkareinFragment_to_itemEditKareinFragment)
            }
            nextButton.setOnClickListener {
                findNavController().navigate(R.id.action_saleDarjkareinFragment_to_saleConfirmKareinFragment)
            }
        }
    }
}