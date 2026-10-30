package com.example.easykhata.ui.fragments.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentItemEditKareinBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemEditKareinFragment : Fragment(R.layout.fragment_item_edit_karein) {
    lateinit var binding: FragmentItemEditKareinBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentItemEditKareinBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            itemEditKareinToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}