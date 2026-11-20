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
import androidx.navigation.fragment.navArgs
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.FragmentMyDetailsBinding
import com.example.easykhata.ui.viewModels.account.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
@AndroidEntryPoint
class MyDetailsFragment : Fragment(R.layout.fragment_my_details) {
    private lateinit var binding: FragmentMyDetailsBinding
    private val args: MyDetailsFragmentArgs by navArgs()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyDetailsBinding.bind(view)
        setUpClicks()
        setUpUi()
        observeUpdateNameState()
    }
    private fun setUpClicks() {
        binding.apply {
            aapKiDetailsToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            myDetailsSaveButton.setOnClickListener {
                val name = aapKaNaamEt.text.toString().trim()
                profileViewModel.updateUserName(name)
            }
        }
    }
    private fun setUpUi() {
        binding.apply {
            aapKaNaamEt.setText(args.user.name)
            aapkaPhone.text = args.user.phoneNumber.replaceFirst(
                Regex("^\\\\+92"),
                "0"
            )
        }
    }
    private fun observeUpdateNameState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.updateNameState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                "Name updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            profileViewModel.resetUpdateNameState()
                            findNavController().navigateUp()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            profileViewModel.resetUpdateNameState()
                        }
                        is UiState.Idle -> {
                            hideProgress()
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