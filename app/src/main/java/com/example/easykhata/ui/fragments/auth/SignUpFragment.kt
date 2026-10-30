package com.example.easykhata.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    lateinit var binding: FragmentSignUpBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentSignUpBinding.bind(view)
    }
}