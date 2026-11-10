package com.example.easykhata.ui.fragments.expense.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.easykhata.R
import com.example.easykhata.data.models.ExpenseSortOption
import com.example.easykhata.databinding.BottomSheetExpenseFilterBinding
import com.example.easykhata.ui.viewModels.expense.ExpenseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class ExpenseFilterBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetExpenseFilterBinding
    private val viewModel: ExpenseViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetExpenseFilterBinding.inflate(
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
        val selectedChipId = when (viewModel.sortOption.value) {
            ExpenseSortOption.DEFAULT ->
                binding.chipDefault.id
            ExpenseSortOption.NEWEST_FIRST ->
                binding.chipNewestFirst.id
            ExpenseSortOption.OLDEST_FIRST ->
                binding.chipOldestFirst.id
            ExpenseSortOption.AMOUNT_HIGH_TO_LOW ->
                binding.chipAmountHighToLow.id
            ExpenseSortOption.AMOUNT_LOW_TO_HIGH ->
                binding.chipAmountLowToHigh.id
        }
        binding.sortChipGroup.check(selectedChipId)
    }
    private fun setupClicks() {
        binding.apply {
            closeSheet.setOnClickListener {
                dismiss()
            }
            expenseFilterButton.setOnClickListener {
                applyExpenseFilter()
            }
        }
    }
    private fun getSelectedSortOption(): ExpenseSortOption {
        return when (binding.sortChipGroup.checkedChipId) {
            binding.chipDefault.id ->
                ExpenseSortOption.DEFAULT
            binding.chipNewestFirst.id ->
                ExpenseSortOption.NEWEST_FIRST
            binding.chipOldestFirst.id ->
                ExpenseSortOption.OLDEST_FIRST
            binding.chipAmountHighToLow.id ->
                ExpenseSortOption.AMOUNT_HIGH_TO_LOW
            binding.chipAmountLowToHigh.id ->
                ExpenseSortOption.AMOUNT_LOW_TO_HIGH
            else -> ExpenseSortOption.DEFAULT
        }
    }
    private fun applyExpenseFilter() {
        val selectedOption = getSelectedSortOption()
        viewModel.setSortOption(selectedOption)
        dismiss()
    }
    private fun setupChipColors() {
        val backgroundColor = ContextCompat.getColorStateList(
            requireContext(),
            R.color.chip_background_selector
        )
        val chips = listOf(
            binding.chipDefault,
            binding.chipNewestFirst,
            binding.chipOldestFirst,
            binding.chipAmountHighToLow,
            binding.chipAmountLowToHigh
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