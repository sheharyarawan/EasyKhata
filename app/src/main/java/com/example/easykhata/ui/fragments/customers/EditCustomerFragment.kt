package com.example.easykhata.ui.fragments.customers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentEditCustomerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCustomerFragment : Fragment(R.layout.fragment_edit_customer) {
    lateinit var binding: FragmentEditCustomerBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentEditCustomerBinding.bind(view)
    }
}