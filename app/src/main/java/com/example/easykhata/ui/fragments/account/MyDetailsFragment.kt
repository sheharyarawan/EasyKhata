package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentMyDetailsBinding

class MyDetailsFragment : Fragment(R.layout.fragment_my_details) {
    lateinit var binding: FragmentMyDetailsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentMyDetailsBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            aapKiDetailsToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}