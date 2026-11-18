package com.example.easykhata.ui.fragments.sales
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.data.models.sales.SelectedSaleItem
import com.example.easykhata.databinding.FragmentSaleDarjkareinBinding
import com.example.easykhata.ui.adapters.SaleCatalogAdapter
import com.example.easykhata.ui.viewModels.auth.BusinessViewModel
import com.example.easykhata.ui.viewModels.sales.ItemsViewModel
import com.example.easykhata.ui.viewModels.sales.SalesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
@AndroidEntryPoint
class SaleDarjkareinFragment :
    Fragment(R.layout.fragment_sale_darjkarein) {
    private lateinit var binding: FragmentSaleDarjkareinBinding
    private val itemsViewModel: ItemsViewModel by activityViewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private val salesViewModel: SalesViewModel by activityViewModels()
    private lateinit var saleCatalogAdapter: SaleCatalogAdapter
    private var allItems = emptyList<SaleItem>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSaleDarjkareinBinding.bind(view)
        setUpRecyclerView()
        setUpClicks()
        setUpSearch()
        observeItems()
        loadItems()
        observeSelectedSaleItems()
    }
    private fun setUpClicks() {
        binding.apply {
            saleDarjKareinToolbar.setNavigationOnClickListener {
                salesViewModel.clearSelectedSaleItems()
                findNavController().navigateUp()
            }
            nayaItemAddButtonSale.setOnClickListener {
                val action =
                    SaleDarjkareinFragmentDirections
                        .actionSaleDarjkareinFragmentToItemEditKareinFragment(
                            Source.ADD.name
                        )
                findNavController().navigate(action)
            }
            nextButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_saleDarjkareinFragment_to_saleConfirmKareinFragment
                )
            }
        }
    }
    private fun setUpRecyclerView() {
        saleCatalogAdapter = SaleCatalogAdapter(
            onAddClick = { item ->
                salesViewModel.increaseSaleItemQuantity(item)
            },
            onRemoveClick = { item ->
                salesViewModel.decreaseSaleItemQuantity(item)
            }
        )
        binding.catalogRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = saleCatalogAdapter
        }
    }
    private fun observeItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                itemsViewModel.itemsState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.catalogRv.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            allItems = state.data
                            filterItems(
                                binding.searchItemEt.text.toString()
                            )
                        }
                        is UiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UiState.Idle -> Unit
                    }
                }
            }
        }
    }
    private fun observeSelectedSaleItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                salesViewModel.selectedSaleItems.collect { selectedItems ->
                    val searchQuery =
                        binding.searchItemEt.text.toString().trim()
                    val filteredItems = allItems.filter { item ->
                        item.name.contains(
                            searchQuery,
                            ignoreCase = true
                        )
                    }
                    val catalogItems = filteredItems.map { item ->
                        SelectedSaleItem(
                            item = item,
                            quantity = selectedItems
                                .find { it.item.id == item.id }
                                ?.quantity ?: 0
                        )
                    }
                    saleCatalogAdapter.submitList(catalogItems)
                    val totalQuantity = selectedItems.sumOf {
                        it.quantity
                    }
                    val totalAmount = selectedItems.sumOf {
                        it.item.price * it.quantity
                    }
                    binding.itemCount.text = totalQuantity.toString()
                    binding.totalSaleAmount.text = getString(
                        R.string.rs_6,
                        DecimalFormat("#,##0.##").format(totalAmount)
                    )
                    updateNextButton(selectedItems.isNotEmpty())
                }
            }
        }
    }
    private fun setUpSearch() {
        binding.searchItemEt.doAfterTextChanged { editable ->
            filterItems(editable?.toString().orEmpty())
        }
    }
    private fun filterItems(query: String) {
        val searchQuery = query.trim()
        val filteredItems = allItems.filter { item ->
            item.name.contains(searchQuery, ignoreCase = true)
        }
        updateCatalog(filteredItems)
    }
    private fun updateCatalog(items: List<SaleItem>) {
        val selectedItems = salesViewModel.selectedSaleItems.value
        val catalogItems = items.map { item ->
            SelectedSaleItem(
                item = item,
                quantity = selectedItems
                    .find { it.item.id == item.id }
                    ?.quantity ?: 0
            )
        }
        saleCatalogAdapter.submitList(catalogItems)
        binding.apply {
            catalogRv.visibility =
                if (items.isEmpty()) View.GONE else View.VISIBLE
            catalogItemCount.text =
                getString(R.string.items, items.size)
            catalogCount.text = items.size.toString()
        }
    }
    private fun updateNextButton(hasSelectedItems: Boolean) {
        binding.nextButton.apply {
            isEnabled = hasSelectedItems
            setTextColor(android.graphics.Color.WHITE)
            backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    if (hasSelectedItems)
                        android.graphics.Color.BLACK
                    else
                        android.graphics.Color.LTGRAY
                )
        }
    }
    private fun loadItems() {
        val businessId = businessViewModel.businessId.value
        if (businessId.isNullOrBlank()) {
            Toast.makeText(
                requireContext(),
                "Business not selected",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        itemsViewModel.observeItems(businessId)
    }
}