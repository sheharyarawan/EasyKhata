package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentPuranaRecordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PuranaRecordFragment : Fragment(R.layout.fragment_purana_record) {
lateinit var binding: FragmentPuranaRecordBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentPuranaRecordBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            puranaRecordToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}