package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentKarobarAddressBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KarobarAddressFragment : Fragment(R.layout.fragment_karobar_address) {
lateinit var binding: FragmentKarobarAddressBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentKarobarAddressBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            locationToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}