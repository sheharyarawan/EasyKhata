package com.example.easykhata.ui.fragments.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentReciptBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReciptFragment : Fragment(R.layout.fragment_recipt) {
    lateinit var binding: FragmentReciptBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentReciptBinding.bind(view)
    }
}