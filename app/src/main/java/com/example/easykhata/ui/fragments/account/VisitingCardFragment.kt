package com.example.easykhata.ui.fragments.account
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.Source
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.account.VisitingCardUiState
import com.example.easykhata.databinding.FragmentVisitingCardBinding
import com.example.easykhata.ui.adapters.VisitingCardAdapter
import com.example.easykhata.ui.viewModels.account.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class VisitingCardFragment : Fragment(R.layout.fragment_visiting_card) {
    private lateinit var binding: FragmentVisitingCardBinding
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var visitingCardAdapter: VisitingCardAdapter
    private var currentCard = VisitingCardUiState()
    private var isUpdatingFields = false
    private val cardBackgrounds = listOf(
        R.color.navy_blue,
        R.color.forest_green,
        R.color.black,
        R.color.maroon,
        R.color.deep_purple
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVisitingCardBinding.bind(view)
        setUpRecyclerView()
        setUpClicks()
        setUpTextWatchers()
        observeProfileData()
        loadData()
    }
    private fun setUpClicks() {
        binding.apply {
            visitingCardBusinessAddress.setOnClickListener {
                findNavController().navigate(
                    R.id.action_visitingCardFragment_to_karobarAddressFragment
                )
            }
            visitingCardToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            val navigateToType = View.OnClickListener {
                val action= VisitingCardFragmentDirections.actionVisitingCardFragmentToKarobarTypeFragment(
                    Source.EDIT
                )
                findNavController().navigate(
                    action
                )
            }
            tvBusinessTypeLabel.setOnClickListener(navigateToType)
            visitingCardBusinessType.setOnClickListener(navigateToType)
        }
    }
    private fun setUpRecyclerView() {
        visitingCardAdapter = VisitingCardAdapter()
        binding.visitingCardRv.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = visitingCardAdapter
        }
        updateVisitingCards()
    }
    private fun observeProfileData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    profileViewModel.userState.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                currentCard = currentCard.copy(
                                    ownerName = state.data.name,
                                    phoneNumber = state.data.phoneNumber
                                )
                                updateInputFields()
                                updateVisitingCards()
                            }
                            is UiState.Error -> {
                                // Handle error if required
                            }
                            else -> Unit
                        }
                    }
                }
                launch {
                    profileViewModel.businessInfoState.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                currentCard = currentCard.copy(
                                    businessName = state.data.businessName,
                                    businessType = state.data.businessType,
                                    address = state.data.businessAddress
                                )
                                updateInputFields()
                                updateVisitingCards()
                            }
                            is UiState.Error -> {
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
    private fun updateInputFields() {
        isUpdatingFields = true
        binding.apply {
            visitingCardName.setText(currentCard.businessName)
            visitingCardOwnerName.setText(currentCard.ownerName)
            visitingCardPhoneNumber.setText(currentCard.phoneNumber)
            visitingCardBusinessType.text = currentCard.businessType
                .ifBlank { getString(R.string.select_business_type) }
            visitingCardBusinessAddress.text = currentCard.address
                .ifBlank { getString(R.string.karobar_ka_address_2) }
        }
        isUpdatingFields = false
    }
    private fun setUpTextWatchers() {
        binding.apply {
            visitingCardName.doAfterTextChanged { text ->
                if (!isUpdatingFields) {
                    currentCard = currentCard.copy(
                        businessName = text.toString()
                    )
                    updateVisitingCards()
                }
            }
            visitingCardOwnerName.doAfterTextChanged { text ->
                if (!isUpdatingFields) {
                    currentCard = currentCard.copy(
                        ownerName = text.toString()
                    )
                    updateVisitingCards()
                }
            }
            visitingCardPhoneNumber.doAfterTextChanged { text ->
                if (!isUpdatingFields) {
                    currentCard = currentCard.copy(
                        phoneNumber = text.toString()
                    )
                    updateVisitingCards()
                }
            }
        }
    }
    private fun updateVisitingCards() {
        val cards = cardBackgrounds.map { background ->
            currentCard.copy(backgroundColor = background)
        }
        visitingCardAdapter.submitList(cards)
    }
    private fun loadData(){
        profileViewModel.loadUserInfo()
        profileViewModel.loadActiveBusinessInfo()
    }
}