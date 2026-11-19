package com.example.easykhata.ui.fragments.account.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.databinding.BottomSheetKarobarBadleinBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class KarobarBadleinBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetKarobarBadleinBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetKarobarBadleinBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClicks()
    }
    private fun setupClicks(){
        binding.karobarBadlein.setOnClickListener {
            dismiss()
            KarobarNaamBottomSheet().show(
                parentFragmentManager,
                "KarobarNaamBottomSheet"
            )
        }
    }
}