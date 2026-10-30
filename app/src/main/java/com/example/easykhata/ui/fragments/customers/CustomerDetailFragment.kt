package com.example.easykhata.ui.fragments.customers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentAddCustomerDetailBinding
import com.example.easykhata.databinding.FragmentCustomerDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerDetailFragment : Fragment(R.layout.fragment_customer_detail) {
lateinit var binding: FragmentCustomerDetailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentCustomerDetailBinding.bind(view)
    }
}