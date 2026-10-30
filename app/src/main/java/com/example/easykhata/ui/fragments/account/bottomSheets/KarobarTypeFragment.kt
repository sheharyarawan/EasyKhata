package com.example.easykhata.ui.fragments.account.bottomSheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentKarobarTypeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KarobarTypeFragment : Fragment(R.layout.fragment_karobar_type) {
lateinit var binding: FragmentKarobarTypeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentKarobarTypeBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            karobarTypeToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}