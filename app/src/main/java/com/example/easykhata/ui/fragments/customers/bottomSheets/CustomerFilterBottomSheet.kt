package com.example.easykhata.ui.fragments.customers.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.core.content.ContextCompat
import com.example.easykhata.R
import com.example.easykhata.data.models.CustomerBalanceFilter
import com.example.easykhata.databinding.BottomSheetCustomerFilterBinding
import com.example.easykhata.ui.viewModels.customer.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class CustomerFilterBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetCustomerFilterBinding
    private val viewModel: HomeViewModel by activityViewModels()
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChipColors()
        setupDefaultSelection()
        setupClicks()
    }
    private fun setupDefaultSelection() {
        val selectedChipId = when (viewModel.customerFilter.value) {
            CustomerBalanceFilter.ALL -> binding.chipAll.id
            CustomerBalanceFilter.CLEAR -> binding.chipHisaabClear.id
            CustomerBalanceFilter.MAINE_DENE -> binding.chipMaineDene.id
            CustomerBalanceFilter.MAINE_LENE -> binding.chipMaineLene.id
        }
        binding.sortChipGroupCustomer.check(selectedChipId)
    }
    private fun setupClicks() {
        binding.apply {
            customerFilterKareinButton.setOnClickListener {
                applyCustomerFilter()
            }
        }
    }
    private fun getSelectedFilter(): CustomerBalanceFilter {
        return when (binding.sortChipGroupCustomer.checkedChipId) {
            binding.chipHisaabClear.id ->
                CustomerBalanceFilter.CLEAR
            binding.chipMaineDene.id ->
                CustomerBalanceFilter.MAINE_DENE
            binding.chipMaineLene.id ->
                CustomerBalanceFilter.MAINE_LENE
            else -> CustomerBalanceFilter.ALL
        }
    }
    private fun applyCustomerFilter() {
        viewModel.setCustomerFilter(getSelectedFilter())
        dismiss()
    }
    private fun setupChipColors() {
        val backgroundColor = ContextCompat.getColorStateList(
            requireContext(),
            R.color.chip_background_selector
        )
        val chips = listOf(
            binding.chipAll,
            binding.chipHisaabClear,
            binding.chipMaineDene,
            binding.chipMaineLene
        )
        chips.forEach { chip ->
            chip.chipBackgroundColor = backgroundColor
            chip.setTextColor(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.chip_text_selector
                )
            )
        }
    }
}