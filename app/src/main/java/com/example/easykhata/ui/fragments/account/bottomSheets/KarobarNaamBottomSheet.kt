package com.example.easykhata.ui.fragments.account.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.BottomSheetKarobarNaamBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
class KarobarNaamBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetKarobarNaamBinding
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetKarobarNaamBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        businessViewModel.resetNewBusinessState()
        setupClicks()
        observeNewBusinessState()
    }
    private fun setupClicks() {
        binding.karobarSave.setOnClickListener {
            val businessName = binding.karobarKaNaam.text
                .toString()
                .trim()
            if (businessName.isEmpty()) {
                binding.karobarKaNaam.error =
                    "Please enter karobar name"
                return@setOnClickListener
            }
            binding.karobarKaNaam.error = null
            businessViewModel.createNewBusiness(businessName)
        }
    }
    private fun observeNewBusinessState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                businessViewModel.newBusinessState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideProgress()
                            binding.karobarSave.isEnabled = true
                        }
                        is UiState.Loading -> {
                            showProgress()
                            binding.karobarSave.isEnabled = false
                        }
                        is UiState.Success -> {
                            hideProgress()
                            binding.karobarSave.isEnabled = true
                            dismiss()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            binding.karobarSave.isEnabled = true
                            binding.karobarKaNaam.error = state.message
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