package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentAnayWaleBinding

class AnayWaleFragment : Fragment(R.layout.fragment_anay_wale) {
    lateinit var binding: FragmentAnayWaleBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentAnayWaleBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            udhaarWasooliTareekhButton.setOnClickListener {
                findNavController().navigate(R.id.action_anayWaleFragment_to_wasooliCustomerFragment)
            }
        }
    }

}