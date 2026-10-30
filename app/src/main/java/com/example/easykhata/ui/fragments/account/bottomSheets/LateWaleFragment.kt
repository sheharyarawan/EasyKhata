package com.example.easykhata.ui.fragments.account.bottomSheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentLateWaleBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class LateWaleFragment : Fragment(R.layout.fragment_late_wale) {
    lateinit var binding: FragmentLateWaleBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentLateWaleBinding.bind(view)
    }
}