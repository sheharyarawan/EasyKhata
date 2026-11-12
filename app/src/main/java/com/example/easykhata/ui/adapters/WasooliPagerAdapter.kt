package com.example.easykhata.ui.adapters
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.easykhata.ui.fragments.account.AnayWaleFragment
import com.example.easykhata.ui.fragments.account.bottomSheets.LateWaleFragment
class WasooliPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AnayWaleFragment()
            1 -> LateWaleFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}