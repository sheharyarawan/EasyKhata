package com.example.easykhata.ui.fragments.account
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.auth.User
import com.example.easykhata.databinding.FragmentAccountBinding
import com.example.easykhata.ui.viewModels.account.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {
    private lateinit var binding: FragmentAccountBinding
    private val profileViewModel: ProfileViewModel by activityViewModels()
    lateinit var user: User
    private var isBusinessInfoExpanded = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)
        setUpClicks()
        observeUserInfo()
        observeBusinessInfo()
        loadUserInfo()
        loadActiveBusinessInfo()
    }
    private fun loadUserInfo() {
        profileViewModel.loadUserInfo()
    }
    private fun observeUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            user=state.data
                            setUpUi(user)
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
        }
    }
    private fun observeBusinessInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.businessInfoState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            val business = state.data
                            binding.apply {
                                karobarName.text = business.businessName
                                karobarType.text = business.businessType.ifBlank { "Karobar Type" }
                                accountToolbar.title=business.businessName.uppercase()
                            }
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
        }
    }
    private fun setUpUi(user: User) {
        binding.apply {
            tvAccountName.text = user.name
            tvMobileNumber.text = user.phoneNumber.replaceFirst(
                Regex("^\\+92"),
                "0"
            )
        }
    }
    private fun setUpClicks() {
        binding.apply {
            visitingIv.setOnClickListener {
                findNavController().navigate(
                    R.id.action_accountFragment_to_visitingCardFragment
                )
            }
            wasooliIv.setOnClickListener {
                findNavController().navigate(
                    R.id.action_accountFragment_to_wasooliFragment
                )
            }
            cashBookIv.setOnClickListener {
                findNavController().navigate(
                    R.id.action_accountFragment_to_cashbookFragment
                )
            }
            editAccountCardView.setOnClickListener {
                val action= AccountFragmentDirections.actionAccountFragmentToMyDetailsFragment(
                    user
                )
                findNavController().navigate(action)
            }
            karobarKiMaloomat.setOnClickListener {
                isBusinessInfoExpanded = !isBusinessInfoExpanded
                karobarMaloomatCard.visibility = if (isBusinessInfoExpanded) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }
    private fun loadActiveBusinessInfo() {
        profileViewModel.loadActiveBusinessInfo()
    }
    private fun showProgress() {
        binding.expenseProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.expenseProgressOverlay.visibility = View.GONE
    }
}