package com.example.easykhata.ui.fragments.account.bottomSheets

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentKarobarTypeBinding
import com.example.easykhata.ui.adapters.KarobarTypeAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class KarobarTypeFragment : Fragment(R.layout.fragment_karobar_type) {
    private lateinit var binding: FragmentKarobarTypeBinding
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private lateinit var karobarTypeAdapter: KarobarTypeAdapter
    private var karobarTypes: List<String> = emptyList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKarobarTypeBinding.bind(view)
        setUpRecyclerView()
        setUpClicks()
    }
    private fun setUpRecyclerView() {
        karobarTypes = businessViewModel.getKarobarTypes()
        karobarTypeAdapter = KarobarTypeAdapter()
        binding.karobarTypeRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = karobarTypeAdapter
        }
        karobarTypeAdapter.submitList(karobarTypes)
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
                businessViewModel.setSelectedKarobarType(selectedType)
                findNavController().navigateUp()
            }
        }
    }
}