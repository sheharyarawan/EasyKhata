package com.example.easykhata.ui.fragments.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.FragmentOtpBinding
import com.example.easykhata.ui.viewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
@AndroidEntryPoint
class OtpFragment : Fragment(R.layout.fragment_otp) {
    private lateinit var binding: FragmentOtpBinding
    private val args: OtpFragmentArgs by navArgs()
    private var errorCardJob: Job? = null
    private val authViewModel: AuthViewModel by viewModels()
    private var verificationId: String? = null
    private var isVerifying = false
    private lateinit var otpFields: List<EditText>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpBinding.bind(view)
        verificationId = args.verificationId
        binding.otpCardView.visibility = View.GONE
        setUpClicks()
        setUpOtpFields()
        observeUiState()
        observeBusinessState()
    }
    private fun setUpClicks() {
        binding.apply {
            otpToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    private fun setUpOtpFields() {
        otpFields = listOf(
            binding.otp1,
            binding.otp2,
            binding.otp3,
            binding.otp4,
            binding.otp5,
            binding.otp6
        )
        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}
                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {}
                override fun afterTextChanged(s: Editable?) {
                    if (isVerifying) return
                    if (!s.isNullOrEmpty() && index < otpFields.lastIndex) {
                        otpFields[index + 1].requestFocus()
                    }
                    val otp = otpFields.joinToString("") {
                        it.text.toString()
                    }
                    if (otp.length == 6 && otp.all { it.isDigit() }) {
                        verifyOtpAutomatically()
                    }
                }
            })
        }
    }
    private fun verifyOtpAutomatically() {
        val otp = otpFields.joinToString("") {
            it.text.toString()
        }
        val id = verificationId
        if (otp.length != 6 || id == null || isVerifying) return
        isVerifying = true
        authViewModel.verifyOtp(id, otp)
    }
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            isVerifying = false
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            isVerifying = true
                            showProgress()
                        }
                        is UiState.Success -> {
                            isVerifying = false
                            showProgress()
                            authViewModel.checkExistingBusiness()
                        }
                        is UiState.Error -> {
                            isVerifying = false
                            hideProgress()
                            otpFields.forEach {
                                it.text?.clear()
                            }
                            otpFields.firstOrNull()?.requestFocus()
                            showErrorCard()
                        }
                    }
                }
            }
        }
    }
    private fun observeBusinessState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.businessState.collect { state ->
                    when (state) {
                        is UiState.Idle -> Unit
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            if (state.data) {
                                findNavController().navigate(
                                    R.id.home_graph
                                )
                            } else {
                                findNavController().navigate(
                                    R.id.action_otpFragment_to_karobarTafseelFragment
                                )
                            }
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun showErrorCard() {
        errorCardJob?.cancel()
        errorCardJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(200.milliseconds)
            binding.otpErrorCard.visibility = View.VISIBLE
            delay(2000.milliseconds)
            binding.otpErrorCard.visibility = View.GONE
        }
    }
    private fun showProgress() {
        binding.apply {
            otpCardView.visibility = View.VISIBLE
            otpErrorCard.visibility = View.GONE
            otpProgressBar.visibility = View.VISIBLE
        }
    }
    private fun hideProgress() {
        binding.otpCardView.visibility = View.GONE
    }
}