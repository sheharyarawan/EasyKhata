package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentWasooliCustomerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WasooliCustomerFragment : Fragment(R.layout.fragment_wasooli_customer) {
    lateinit var binding: FragmentWasooliCustomerBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentWasooliCustomerBinding.bind(view)
    }
}