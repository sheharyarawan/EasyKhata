package com.example.easykhata.ui.fragments.customers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentAddCustomerDetailBinding
class AddCustomerDetailFragment : Fragment(R.layout.fragment_add_customer_detail) {
    lateinit var binding: FragmentAddCustomerDetailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentAddCustomerDetailBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            addCustomerDetailToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}