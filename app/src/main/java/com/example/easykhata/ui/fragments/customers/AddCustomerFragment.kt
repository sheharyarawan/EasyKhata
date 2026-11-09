package com.example.easykhata.ui.fragments.customers
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easykhata.R
import com.example.easykhata.data.models.UiState
import com.example.easykhata.data.models.customers.Contact
import com.example.easykhata.data.models.customers.Customer
import com.example.easykhata.data.models.customers.PhoneContactUiModel
import com.example.easykhata.databinding.FragmentAddCustomerBinding
import com.example.easykhata.ui.adapters.PhoneContactsAdapter
import com.example.easykhata.ui.viewModels.BusinessViewModel
import com.example.easykhata.ui.viewModels.customerViewModel.AddCustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class AddCustomerFragment : Fragment(R.layout.fragment_add_customer) {
    private lateinit var binding: FragmentAddCustomerBinding
    private val viewModel: AddCustomerViewModel by viewModels()
    private val businessViewModel: BusinessViewModel by activityViewModels()
    private var phoneContacts: List<Contact> = emptyList()
    private var existingCustomerPhones: Set<String> = emptySet()
    private lateinit var phoneContactsAdapter: PhoneContactsAdapter
    private val contactsPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                viewModel.loadContacts()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Contacts permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddCustomerBinding.bind(view)
        setUpClicks()
        setUpRecyclerView()
        observeContacts()
        observeExistingCustomerPhones()
        observeBusinessId()
        observeAddCustomerState()
        checkContactsPermission()
    }
    private fun setUpClicks() {
        binding.apply {
            addCustomerToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            addNewCustomer.setOnClickListener {
                findNavController().navigate(
                    R.id.action_addCustomerFragment_to_addCustomerDetailFragment
                )
            }
        }
    }
    private fun setUpRecyclerView() {
        phoneContactsAdapter = PhoneContactsAdapter { contact ->
            val businessId = businessViewModel.businessId.value
            if (businessId.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Please select a business first",
                    Toast.LENGTH_SHORT
                ).show()
                return@PhoneContactsAdapter
            }
            val customer = Customer(
                name = contact.name,
                phone = contact.phoneNumber
            )
            viewModel.addCustomer(businessId, customer)
        }
        binding.phoneCustomers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = phoneContactsAdapter
        }
    }
    private fun checkContactsPermission() {
        val permissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (permissionGranted) {
            viewModel.loadContacts()
        } else {
            contactsPermissionLauncher.launch(
                Manifest.permission.READ_CONTACTS
            )
        }
    }
    private fun observeContacts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contactsState.collect { state ->
                    when (state) {
                        is UiState.Idle -> Unit
                        is UiState.Loading -> Unit
                        is UiState.Success -> {
                            phoneContacts = state.data
                            updateContactList()
                        }
                        is UiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun updateContactList() {
        val contactItems = phoneContacts.map { contact ->
            PhoneContactUiModel(
                contact = contact,
                isAdded = contact.phoneNumber.filter(Char::isDigit)
                        in existingCustomerPhones
            )
        }
        phoneContactsAdapter.submitList(contactItems)
    }
    private fun observeBusinessId() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                businessViewModel.businessId.collect { businessId ->
                    if (businessId.isNullOrBlank()) {
                        existingCustomerPhones = emptySet()
                        updateContactList()
                    } else {
                        viewModel.loadExistingCustomers(businessId)
                    }
                }
            }
        }
    }
    private fun observeExistingCustomerPhones() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.existingCustomerPhones.collect { phones ->
                    existingCustomerPhones = phones
                    updateContactList()
                }
            }
        }
    }
    private fun observeAddCustomerState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addCustomerState.collect { state ->
                    when (state) {
                        null -> {
                            hideProgress()
                        }
                        is UiState.Idle -> {
                            hideProgress()
                        }
                        is UiState.Loading -> {
                            showProgress()
                        }
                        is UiState.Success -> {
                            hideProgress()
                            val customerId = state.data
                            viewModel.resetAddCustomerState()
                            val action =
                                AddCustomerFragmentDirections
                                    .actionAddCustomerFragmentToCustomerDetailFragment(
                                        customerId
                                    )
                            findNavController().navigate(action)
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetAddCustomerState()
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