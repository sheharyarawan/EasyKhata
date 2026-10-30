package com.example.easykhata.ui.fragments.sales
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentSalesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SalesFragment : Fragment(R.layout.fragment_sales) {
    lateinit var binding: FragmentSalesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentSalesBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            itemsManageCard.setOnClickListener {
                findNavController().navigate(R.id.action_salesFragment_to_itemsManageFragment)
            }
            saleDarjKareinButton.setOnClickListener {
                findNavController().navigate(R.id.action_salesFragment_to_saleDarjkareinFragment)
            }
        }
    }
}