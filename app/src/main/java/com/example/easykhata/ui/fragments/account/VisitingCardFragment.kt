package com.example.easykhata.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentVisitingCardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitingCardFragment : Fragment(R.layout.fragment_visiting_card) {
    lateinit var binding: FragmentVisitingCardBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentVisitingCardBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            visitingCardBusinessAddress.setOnClickListener {
                findNavController().navigate(R.id.action_visitingCardFragment_to_karobarAddressFragment)
            }
            visitingCardToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            val navigateToType= View.OnClickListener{
                findNavController().navigate(R.id.action_visitingCardFragment_to_karobarTypeFragment)
            }
            tvBusinessTypeLabel.setOnClickListener(navigateToType)
            visitingCardBusinessType.setOnClickListener(navigateToType)
        }
    }
}