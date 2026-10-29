package com.example.easykhata.ui.fragments.customers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentHomeBinding
class HomeFragment : Fragment(R.layout.fragment_home) {
lateinit var binding: FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentHomeBinding.bind(view)
    }
}