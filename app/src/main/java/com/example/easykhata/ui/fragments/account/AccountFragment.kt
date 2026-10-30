package com.example.easykhata.ui.fragments.account
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentAccountBinding
class AccountFragment : Fragment(R.layout.fragment_account) {
    lateinit var binding: FragmentAccountBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentAccountBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            visitingIv.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_visitingCardFragment)
            }
            wasooliIv.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_wasooliFragment)
            }
            cashBookIv.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_cashbookFragment)
            }
            editAccountCardView.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_myDetailsFragment)
            }
        }
    }
}