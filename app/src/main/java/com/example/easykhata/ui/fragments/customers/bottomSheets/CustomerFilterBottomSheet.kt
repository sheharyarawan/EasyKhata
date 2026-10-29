package com.example.easykhata.ui.fragments.customers.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easykhata.databinding.BottomSheetCustomerFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class CustomerFilterBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetCustomerFilterBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCustomerFilterBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
}