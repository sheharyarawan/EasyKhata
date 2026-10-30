package com.example.easykhata.ui.fragments.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentItemsManageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemsManageFragment : Fragment(R.layout.fragment_items_manage) {
lateinit var binding: FragmentItemsManageBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentItemsManageBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            mereItemsToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            nayaItemAddButton.setOnClickListener {
                findNavController().navigate(R.id.action_itemsManageFragment_to_itemEditKareinFragment)
            }
        }
    }
}