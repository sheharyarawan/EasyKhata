package com.example.easykhata.ui.fragments.sales
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.Source
import com.example.easykhata.data.models.UiState
import com.example.easykhata.databinding.FragmentItemsManageBinding
import com.example.easykhata.ui.adapters.ItemsAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.sales.ItemsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.content.Context
import com.example.easykhata.data.models.sales.SaleItem
import androidx.core.view.isVisible
@AndroidEntryPoint
class ItemsManageFragment : Fragment(R.layout.fragment_items_manage) {
    private lateinit var binding: FragmentItemsManageBinding
    private val viewModel: ItemsViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private var allItems = emptyList<SaleItem>()
    private lateinit var itemsAdapter: ItemsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemsManageBinding.bind(view)
        setUpClicks()
        setUpRecyclerView()
        observeItems()
        load()
        setUpSearch()
    }
    private fun setUpClicks() {
        binding.apply {
            mereItemsToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            nayaItemAddButton.setOnClickListener {
                navigateToAddItem()
            }
            salesItemAddKarein.setOnClickListener {
                navigateToAddItem()
            }
        }
    }
    private fun navigateToAddItem() {
        val action =
            ItemsManageFragmentDirections
                .actionItemsManageFragmentToItemEditKareinFragment(
                    Source.ADD.name
                )
        findNavController().navigate(action)
    }
    private fun setUpRecyclerView() {
        itemsAdapter = ItemsAdapter(
            onItemClick = { item ->
                val action = ItemsManageFragmentDirections.actionItemsManageFragmentToItemEditKareinFragment(
                    Source.EDIT.name,item.id
                )
                findNavController().navigate(action)
            },
            onMenuClick = { item, anchor ->
                // Show popup card
            }
        )
        binding.mereItemsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemsAdapter
        }
    }
    private fun observeItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.itemsState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            showLoading()
                        }
                        is UiState.Success -> {
                            hideLoading()
                            val items = state.data
                            allItems = items
                            filterItems(binding.itemSearchEt.text.toString())
                            updateItemsVisibility(items.isEmpty())
                            binding.mereItemsToolbar.subtitle =
                                "Catalog mein ${items.size}"
                        }
                        is UiState.Error -> {
                            hideLoading()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UiState.Idle -> {
                            hideLoading()
                        }
                    }
                }
            }
        }
    }
    private fun load() {
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank()) {
            return
        }
        viewModel.observeItems(businessId)
    }
    private fun setUpSearch() {
        binding.mereItemsToolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.search) {
                val isVisible =
                    binding.itemSearchEt.isVisible
                if (isVisible) {
                    binding.apply {
                        itemSearchEt.text.clear()
                        itemSearchEt.visibility = View.GONE
                        materialCardView13.visibility = View.GONE
                    }
                    itemsAdapter.submitList(allItems)

                } else {
                    binding.apply {
                        materialCardView13.visibility = View.VISIBLE
                        itemSearchEt.visibility = View.VISIBLE
                        itemSearchEt.requestFocus()
                    }
                }
                true
            } else {
                false
            }
        }
        binding.itemSearchEt.addTextChangedListener(object : TextWatcher {
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
                filterItems(s.toString())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }
    private fun filterItems(query: String) {
        val filteredItems = if (query.isBlank()) {
            allItems
        } else {
            allItems.filter { item ->
                item.name.contains(query.trim(), ignoreCase = true)
            }
        }
        itemsAdapter.submitList(filteredItems)
    }
    private fun showLoading() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideLoading() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
    private fun updateItemsVisibility(isEmpty: Boolean) {
        binding.apply {
            emptyItemsLayout.visibility =
                if (isEmpty) View.VISIBLE else View.GONE
            mereItemsRv.visibility =
                if (isEmpty) View.GONE else View.VISIBLE
            nayaItemAddButton.visibility =
                if (isEmpty) View.GONE else View.VISIBLE
        }
    }
}