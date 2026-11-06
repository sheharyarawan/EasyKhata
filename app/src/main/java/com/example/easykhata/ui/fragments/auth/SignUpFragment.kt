package com.example.easykhata.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.FragmentSignUpBinding
import com.example.easykhata.ui.viewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    lateinit var binding: FragmentSignUpBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentSignUpBinding.bind(view)
        setUpClicks()
        observeUiState()
    }
    private fun setUpClicks(){
        binding.sendOtpBtn.setOnClickListener {
            val phoneNumber = binding.signUpPhoneEt.text.toString().trim()
            if (phoneNumber.length != 10 ||
                !phoneNumber.startsWith("3")
            ) {
                binding.signUpPhoneEt.error =
                    "Durust phone number enter karein"
                return@setOnClickListener
            }
            authViewModel.sendOtp(
                phoneNumber = "+92$phoneNumber",
                activity = requireActivity()
            )
        }
    }
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                authViewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            binding.sendOtpBtn.isEnabled = true
                        }
                        is UiState.Loading -> {
                            binding.sendOtpBtn.isEnabled = false
                            binding.otpProgressBar.visibility = View.VISIBLE
                        }
                        is UiState.Success -> {
                            binding.otpProgressBar.visibility = View.GONE
                            binding.sendOtpBtn.isEnabled = true
                            val data = state.data
                            if (data.verificationId != null) {
                                val action = SignUpFragmentDirections
                                    .actionSignUpFragmentToOtpFragment(
                                        verificationId = data.verificationId,
                                        phoneNumber = "+92${binding.signUpPhoneEt.text.toString().trim()}"
                                    )
                                findNavController().navigate(action)
                            }
                        }
                        is UiState.Error -> {
                            binding.otpProgressBar.visibility = View.GONE
                            binding.sendOtpBtn.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                "Request fail hogi, dubara koshish karein",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}