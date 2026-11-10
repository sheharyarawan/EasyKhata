package com.example.easykhata.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentSplashBinding
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.auth.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.time.Duration.Companion.milliseconds
@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentSplashBinding.bind(view)
        delayTime()
    }
    private fun navigateBasedOnAuthStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            val navController = findNavController()
            if (viewModel.isUserSignedIn() && viewModel.hasBusiness()) {
                businessViewModel.loadActiveBusinessId {
                    if (isAdded) {
                        navController.navigate(R.id.home_graph)
                    }
                }
            } else {
                navController.navigate(R.id.auth_graph)
            }
        }
    }
    private fun delayTime(){
        viewLifecycleOwner.lifecycleScope.launch{
            delay(3000.milliseconds)
            navigateBasedOnAuthStatus()
        }
    }
}