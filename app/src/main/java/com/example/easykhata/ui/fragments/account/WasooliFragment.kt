package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentWasooliBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class WasooliFragment : Fragment(R.layout.fragment_wasooli) {
    lateinit var binding: FragmentWasooliBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentWasooliBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            udhaarKiWasooliToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}