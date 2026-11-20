package com.example.easykhata.ui.fragments.account.bottomSheets
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.Source
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.account.KarobarType
import com.example.easykhata.databinding.FragmentKarobarTypeBinding
import com.example.easykhata.ui.adapters.KarobarTypeAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.account.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class KarobarTypeFragment : Fragment(R.layout.fragment_karobar_type) {
    private lateinit var binding: FragmentKarobarTypeBinding
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var karobarTypeAdapter: KarobarTypeAdapter
    private var karobarTypes: List<String> = emptyList()
    private val args: KarobarTypeFragmentArgs by navArgs()
    private val source: Source
        get() = args.source
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKarobarTypeBinding.bind(view)
        setUpRecyclerView()
        setUpClicks()
        if (source == Source.EDIT) {
            observeUpdateBusinessInfo()
        }
    }
    private fun setUpRecyclerView() {
        karobarTypes = businessViewModel.getKarobarTypes()
        karobarTypeAdapter = KarobarTypeAdapter()
        binding.karobarTypeRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = karobarTypeAdapter
        }
        karobarTypeAdapter.submitList(
            karobarTypes.map { KarobarType(name = it) }
        )
        if (source == Source.EDIT) {
            setPreviouslySelectedType()
        }
    }
    private fun setPreviouslySelectedType() {
        val state = profileViewModel.businessInfoState.value
        if (state is UiState.Success) {
            karobarTypeAdapter.setSelectedType(state.data.businessType)
        }
    }
    private fun setUpClicks() {
        binding.apply {
            karobarTypeToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            karobarSaveButton.setOnClickListener {
                val selectedType = karobarTypeAdapter.getSelectedType()
                if (selectedType == null) {
                    Toast.makeText(
                        requireContext(),
                        "Please select a karobar type",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                when (source) {
                    Source.ADD -> {
                        businessViewModel.setSelectedKarobarType(selectedType)
                        findNavController().navigateUp()
                    }
                    Source.EDIT -> {
                        val state = profileViewModel.businessInfoState.value
                        if (state is UiState.Success) {
                            val business = state.data
                            if (selectedType == business.businessType) {
                                findNavController().navigateUp()
                                return@setOnClickListener
                            }
                            profileViewModel.updateActiveBusinessInfo(
                                businessName = business.businessName,
                                businessType = selectedType,
                                businessAddress = business.businessAddress
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Business information not loaded yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun observeUpdateBusinessInfo() {
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
                            profileViewModel.resetUpdateBusinessInfoState()
                            findNavController().navigateUp()
                        }
                        is UiState.Error -> {
                           hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            profileViewModel.resetUpdateBusinessInfoState()
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