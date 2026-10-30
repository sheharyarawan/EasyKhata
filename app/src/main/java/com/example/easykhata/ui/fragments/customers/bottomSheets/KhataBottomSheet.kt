package com.example.easykhata.ui.fragments.customers.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.databinding.BottomSheetKhataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class KhataBottomSheet: BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetKhataBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetKhataBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
}