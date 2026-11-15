package com.example.easykhata.ui.fragments.sales
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.databinding.FragmentItemEditKareinBinding
import com.example.easykhata.ui.fragments.sales.bottomSheets.UnitBottomSheet
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.sales.ItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ItemEditKareinFragment :
    Fragment(R.layout.fragment_item_edit_karein) {
    private lateinit var binding: FragmentItemEditKareinBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val args: ItemEditKareinFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemEditKareinBinding.bind(view)
        setupUi()
        setupClicks()
        observeSelectedUnit()
        observeAddItemState()
    }
    private fun setupUi() {
        binding.itemEditKareinToolbar.title = "Naya Item"
        binding.itemEditKareinToolbar.subtitle = "Apne catalog mein add karein"
        binding.tabdeeliyaSaveButton.text="Tabdeeliyan save karein"
    }
    private fun setupClicks() {
        binding.itemEditKareinToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.tabdeeliyaSaveButton.setOnClickListener {
            addItem()
        }
        binding.itemUnit.setOnClickListener {
            UnitBottomSheet().show(
                parentFragmentManager,
                "UnitBottomSheet"
            )
        }
    }
    private fun observeSelectedUnit() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedUnit.collect { unit ->
                    binding.itemUnit.text = "${unit.name} (${unit.symbol})"
                }
            }
        }
    }
    private fun addItem() {
        val name = binding.itemKaNaamEt.text.toString().trim()
        val price = binding.itemPriceEt.text.toString().trim().toDoubleOrNull()
        if (name.isEmpty()) {
            binding.itemKaNaamEt.error = "Please enter item name"
            binding.itemKaNaamEt.requestFocus()
            return
        }
        if (price == null || price <= 0.0) {
            binding.itemPriceEt.error = "Please enter a valid price"
            binding.itemPriceEt.requestFocus()
            return
        }
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Please select a business first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val selectedUnit = viewModel.selectedUnit.value
        val item = SaleItem(
            name = name,
            price = price,
            unit = selectedUnit.name,
            unitSymbol = selectedUnit.symbol
        )
        viewModel.addItem(businessId, item)
    }
    private fun observeAddItemState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addItemState.collect { state ->
                    when (state) {
                        is UiState.Idle -> hideProgress()
                        is UiState.Loading -> showProgress()
                        is UiState.Success -> {
                            hideProgress()
                            viewModel.resetAddItemState()
                            findNavController().navigateUp()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetAddItemState()
                        }
                    }
                }
            }
        }
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}