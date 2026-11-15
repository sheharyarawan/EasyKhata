package com.example.easykhata.ui.fragments.sales.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.databinding.BottomSheetUnitBinding
import com.example.easykhata.ui.adapters.UnitAdapter
import com.example.easykhata.ui.viewModels.sales.ItemsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class UnitBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetUnitBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    private lateinit var unitAdapter: UnitAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetUnitBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClicks()
        observeUnits()
    }
    private fun setupRecyclerView() {
        unitAdapter = UnitAdapter { unit ->
            viewModel.selectUnit(unit)
            dismiss()
        }
        binding.unitRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = unitAdapter
        }
    }
    private fun setupClicks() {
        binding.closeUnitBottomSheet.setOnClickListener {
            dismiss()
        }
    }
    private fun observeUnits() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.units.collect { units ->
                    unitAdapter.submitList(units)
                }
            }
        }
    }
}