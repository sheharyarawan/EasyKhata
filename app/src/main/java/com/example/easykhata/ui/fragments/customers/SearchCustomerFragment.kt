package com.example.easykhata.ui.fragments.customers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentSearchCustomerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchCustomerFragment : Fragment(R.layout.fragment_search_customer) {
    lateinit var binding: FragmentSearchCustomerBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentSearchCustomerBinding.bind(view)
    }
}