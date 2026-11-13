package com.example.easykhata.ui.fragments.account
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentWasooliBinding
import com.example.easykhata.ui.adapters.WasooliPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class WasooliFragment : Fragment(R.layout.fragment_wasooli) {
    lateinit var binding: FragmentWasooliBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentWasooliBinding.bind(view)
        setUpClicks()
        setUpViewPager()
    }
    private fun setUpClicks(){
        binding.apply {
            udhaarKiWasooliToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    private fun setUpViewPager() {
        binding.wasooliViewPager.adapter = WasooliPagerAdapter(this)
        TabLayoutMediator(
            binding.wasooliTabLayout,
            binding.wasooliViewPager
        ) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.anay_wale)
                1 -> getString(R.string.late_wale)
                else -> ""
            }
        }.attach()
    }
}