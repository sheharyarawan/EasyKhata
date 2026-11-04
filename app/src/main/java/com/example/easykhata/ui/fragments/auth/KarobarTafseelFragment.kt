package com.example.easykhata.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentKarobarTafseelBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class KarobarTafseelFragment : Fragment(R.layout.fragment_karobar_tafseel) {
lateinit var binding: FragmentKarobarTafseelBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentKarobarTafseelBinding.bind(view)
    }
}