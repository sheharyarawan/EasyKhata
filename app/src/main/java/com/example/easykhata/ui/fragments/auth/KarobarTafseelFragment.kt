package com.example.easykhata.ui.fragments.auth
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.example.easykhata.data.models.auth.Business
import com.example.easykhata.data.models.auth.User
import com.example.easykhata.databinding.FragmentKarobarTafseelBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
@AndroidEntryPoint
class KarobarTafseelFragment : Fragment(R.layout.fragment_karobar_tafseel) {
    private lateinit var binding: FragmentKarobarTafseelBinding
    private val args: KarobarTafseelFragmentArgs by navArgs()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKarobarTafseelBinding.bind(view)
        observeViewModel()
        setUpClicks()
    }
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    businessViewModel.selectedKarobarType.collect { selectedType ->
                        if (selectedType.isNotEmpty()) {
                            binding.tvKarobarType.text = selectedType
                            binding.tvKarobarType.error = null
                        }
                    }
                }
                launch {
                    businessViewModel.registrationState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                binding.aagayBarheinButton.apply {
                                    isEnabled = false
                                    backgroundTintList =
                                        ColorStateList.valueOf(Color.GRAY)
                                }
                            }
                            is UiState.Success -> {
                                binding.aagayBarheinButton.apply {
                                    isEnabled = true
                                    backgroundTintList =
                                        ColorStateList.valueOf(Color.BLACK)
                                }
                                Toast.makeText(
                                    requireContext(),
                                    "Karobar successfully created",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.home_graph)
                            }
                            is UiState.Error -> {
                                binding.aagayBarheinButton.apply {
                                    isEnabled = true
                                    backgroundTintList =
                                        ColorStateList.valueOf(Color.BLACK)
                                }
                                Toast.makeText(
                                    requireContext(),
                                    state.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is UiState.Idle -> {
                                binding.aagayBarheinButton.apply {
                                    isEnabled = true
                                    backgroundTintList =
                                        ColorStateList.valueOf(Color.BLACK)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun setUpClicks() {
        binding.apply {
            tvKarobarType.setOnClickListener {
                findNavController().navigate(
                    R.id.action_karobarTafseelFragment_to_karobarTypeFragment2
                )
            }
            aagayBarheinButton.setOnClickListener {
                createAccount()
            }
        }
    }
    private fun createAccount() {
        val businessName = binding.etKarobarNaam.text.toString().trim()
        val businessType = businessViewModel.selectedKarobarType.value
        if (businessName.isEmpty()) {
            binding.etKarobarNaam.error = "Karobar ka naam darj karein"
            return
        }
        if (businessType.isEmpty()) {
            binding.tvKarobarType.error = "Karobar ki qisam select karein"
            return
        }
        val user = User(phoneNumber = args.phoneNumber)
        val business = Business(
            businessName = businessName,
            businessType = businessType
        )
        businessViewModel.createUserWithBusiness(
            user = user,
            business = business
        )
    }
}