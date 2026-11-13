package com.example.easykhata.ui.fragments.customers.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.easykhata.data.models.KhataReportFilter
import com.example.easykhata.databinding.BottomSheetKhataBinding
import com.example.easykhata.ui.viewModels.expense.KhataReportViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class KhataBottomSheet: BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetKhataBinding
    private val khataReportViewModel: KhataReportViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = BottomSheetKhataBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupClicks()
    }
    private fun setupClicks() {
        binding.apply {
            khataReportAll.setOnClickListener {
                selectFilter(KhataReportFilter.ALL)
            }
            khataReportSingleDay.setOnClickListener {
                selectFilter(KhataReportFilter.SINGLE_DAY)
            }
            khataReportPichlaHafta.setOnClickListener {
                selectFilter(KhataReportFilter.PICHLA_HAFTA)
            }
            khataReportPichlaMahina.setOnClickListener {
                selectFilter(KhataReportFilter.PICHLA_MAHINA)
            }
        }
    }
    private fun selectFilter(filter: KhataReportFilter) {
        khataReportViewModel.setFilter(filter)
        dismiss()
    }
}