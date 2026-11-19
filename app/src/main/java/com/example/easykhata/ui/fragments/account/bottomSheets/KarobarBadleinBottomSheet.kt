package com.example.easykhata.ui.fragments.account.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.auth.BusinessUi
import com.example.easykhata.databinding.BottomSheetKarobarBadleinBinding
import com.example.easykhata.ui.adapters.KarobarBadleinAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
class KarobarBadleinBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetKarobarBadleinBinding
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private lateinit var businessAdapter: KarobarBadleinAdapter
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
        businessViewModel.resetSwitchBusinessState()
        setupRecyclerView()
        setupClicks()
        observeBusinesses()
    }
    private fun setupRecyclerView() {
        businessAdapter = KarobarBadleinAdapter(
            onBusinessClick = { businessUi ->
                if (!businessUi.isActive) {
                    businessViewModel.switchBusiness(
                        businessUi.business.businessId
                    )
                }
            },
            onDeleteClick = { businessUi ->
                // Business deletion will be implemented later.
            }
        )
        binding.karobarBadleinRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = businessAdapter
        }
    }
    private fun observeBusinesses() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    businessViewModel.businessId.collect {
                        updateBusinessList()
                    }
                }
                launch {
                    businessViewModel.businessesState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                showProgress()
                            }
                            is UiState.Success -> {
                                hideProgress()
                                updateBusinessList()
                            }
                            is UiState.Error -> {
                                hideProgress()
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is UiState.Idle -> {
                                hideProgress()
                            }
                        }
                    }
                }
                launch {
                    businessViewModel.switchBusinessState.collect { state ->
                        when (state) {
                            is UiState.Idle -> {
                                hideProgress()
                            }
                            is UiState.Loading -> {
                                showProgress()
                            }
                            is UiState.Success -> {
                                hideProgress()
                                dismiss()
                            }
                            is UiState.Error -> {
                                hideProgress()
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
    private fun updateBusinessList() {
        val state = businessViewModel.businessesState.value
        val activeBusinessId = businessViewModel.businessId.value
        if (state is UiState.Success) {
            val businessUiList = state.data.map { business ->
                BusinessUi(
                    business = business,
                    isActive = business.businessId == activeBusinessId
                )
            }
            businessAdapter.submitList(businessUiList)
        }
    }
    private fun setupClicks() {
        binding.karobarBadlein.setOnClickListener {
            dismiss()
            KarobarNaamBottomSheet().show(
                parentFragmentManager,
                "KarobarNaamBottomSheet"
            )
        }
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
}