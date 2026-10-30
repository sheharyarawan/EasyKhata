package com.example.easykhata.ui.fragments.customers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentUpdateBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class UpdateFragment : Fragment(R.layout.fragment_update) {
    lateinit var binding: FragmentUpdateBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentUpdateBinding.bind(view)
    }
}