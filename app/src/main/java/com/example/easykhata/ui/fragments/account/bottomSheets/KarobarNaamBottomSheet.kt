package com.example.easykhata.ui.fragments.account.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.easykhata.data.models.Source
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.BottomSheetKarobarNaamBinding
import com.example.easykhata.ui.viewModels.account.ProfileViewModel
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
class KarobarNaamBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetKarobarNaamBinding
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val source: Source
        get() = if (android.os.Build.VERSION.SDK_INT >= 33) {
            arguments?.getSerializable("source", Source::class.java) ?: Source.ADD
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("source") as? Source ?: Source.ADD
        }
    private val currentBusinessName: String
        get() = arguments?.getString("businessName").orEmpty()
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
        setupInitialUi()
        setupClicks()
        when (source) {
            Source.ADD -> {
                businessViewModel.resetNewBusinessState()
                observeNewBusinessState()
            }
            Source.EDIT -> {
                profileViewModel.resetUpdateBusinessInfoState()
                observeUpdateBusinessInfoState()
            }
        }
    }
    private fun setupInitialUi() {
        binding.apply {
            when (source) {
                Source.ADD -> {
                    karobarKaNaam.setText("")
                    karobarSave.isEnabled = true
                }
                Source.EDIT -> {
                    karobarKaNaam.setText(currentBusinessName)
                }
            }
        }
    }
    private fun setupClicks() {
        binding.apply {
            karobarKaNaam.addTextChangedListener(
                object : android.text.TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) = Unit
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (source == Source.EDIT) {
                            karobarSave.isEnabled =
                                s.toString().trim() != currentBusinessName.trim()
                        }
                    }
                    override fun afterTextChanged(s: android.text.Editable?) = Unit
                }
            )
            karobarSave.setOnClickListener {
                val businessName = karobarKaNaam.text
                    .toString()
                    .trim()
                if (businessName.isEmpty()) {
                    karobarKaNaam.error = "Please enter karobar name"
                    return@setOnClickListener
                }
                karobarKaNaam.error = null
                when (source) {
                    Source.ADD -> {
                        businessViewModel.createNewBusiness(businessName)
                    }
                    Source.EDIT -> {
                        val business = profileViewModel.businessInfoState.value
                        if (business is UiState.Success) {
                            profileViewModel.updateActiveBusinessInfo(
                                businessName = businessName,
                                businessType = business.data.businessType,
                                businessAddress = business.data.businessAddress
                            )
                        }
                    }
                }
            }
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
    private fun observeUpdateBusinessInfoState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.updateBusinessInfoState.collect { state ->
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