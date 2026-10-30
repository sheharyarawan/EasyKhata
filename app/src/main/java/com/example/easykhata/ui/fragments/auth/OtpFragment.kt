package com.example.easykhata.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentOtpBinding
class OtpFragment : Fragment(R.layout.fragment_otp) {
lateinit var binding: FragmentOtpBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentOtpBinding.bind(view)
    }
}