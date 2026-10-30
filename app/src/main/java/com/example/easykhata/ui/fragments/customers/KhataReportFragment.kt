package com.example.easykhata.ui.fragments.customers
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentKhataReportBinding
import com.example.easykhata.ui.fragments.customers.bottomSheets.KhataBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KhataReportFragment : Fragment(R.layout.fragment_khata_report) {
lateinit var binding: FragmentKhataReportBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentKhataReportBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
        binding.apply {
            khataReportToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            khataAll.setOnClickListener {
                KhataBottomSheet().show(
                    parentFragmentManager,
                    "KhataFilterBottomSheet"
                )
            }
        }
    }
}