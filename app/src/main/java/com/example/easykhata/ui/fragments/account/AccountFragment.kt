package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentAccountBinding
class AccountFragment : Fragment(R.layout.fragment_account) {
    lateinit var binding: FragmentAccountBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentAccountBinding.bind(view)
    }
}