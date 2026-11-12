package com.example.easykhata.ui.fragments.customers.bottomSheets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.easykhata.databinding.BottomSheetWasooliBinding
import com.example.easykhata.ui.viewModels.account.WasooliViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import java.util.Calendar
import kotlin.getValue
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import kotlinx.coroutines.launch
import com.example.easykhata.data.models.UiState
class WasooliBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetWasooliBinding
    private val wasooliViewModel: WasooliViewModel by activityViewModels()
    private val customerId by lazy {
        requireArguments().getString(ARG_CUSTOMER_ID).orEmpty()
    }
    private val customerName by lazy {
        requireArguments().getString(ARG_CUSTOMER_NAME).orEmpty()
    }
    private val businessId by lazy {
        requireArguments().getString(ARG_BUSINESS_ID).orEmpty()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetWasooliBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClicks()
        setUpUi()
        observeSaveState()
        observeDeleteState()
    }
    private fun setUpUi(){
        binding.wasooliKisko.text =
            getString(R.string.ko_wasooli_ke_liye_automatic_sms_bheja_jaiga, customerName)
    }
    private fun setupClicks() {
        binding.apply {
            wasooliHafta.setOnClickListener {
                saveReminder(7, Calendar.DAY_OF_YEAR)
            }
            wasooliMahina.setOnClickListener {
                saveReminder(1, Calendar.MONTH)
            }
            wasooliTareekh.setOnClickListener {
                showDatePicker()
            }
            wasooliNoTareekh.setOnClickListener {
                wasooliViewModel.deleteReminder(businessId, customerId)
            }
        }
    }
    private fun observeSaveState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                wasooliViewModel.saveReminderState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showProgress()
                        is UiState.Success -> {
                            hideProgress()
                            wasooliViewModel.resetReminderOperationStates()
                            dismiss()
                            findNavController().navigateUp()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UiState.Idle -> hideProgress()
                    }
                }
            }
        }
    }
    private fun observeDeleteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                wasooliViewModel.deleteReminderState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showProgress()
                        is UiState.Success -> {
                            hideProgress()
                            wasooliViewModel.resetReminderOperationStates()
                            dismiss()
                        }
                        is UiState.Error -> {
                            hideProgress()
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is UiState.Idle -> hideProgress()
                    }
                }
            }
        }
    }
    private fun saveReminder(amount: Int, field: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(field, amount)
        wasooliViewModel.saveReminder(
            businessId,
            customerId,
            Timestamp(calendar.time)
        )
    }
    private fun showDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Wasooli ki tareekh select karein")
            .setCalendarConstraints(constraints)
            .build()
        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            wasooliViewModel.saveReminder(
                businessId,
                customerId,
                Timestamp(java.util.Date(selectedDate))
            )
        }
        datePicker.show(parentFragmentManager, "WasooliDatePicker")
    }
    private fun showProgress() {
        binding.customerProgressOverlay.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.customerProgressOverlay.visibility = View.GONE
    }
    companion object {
        private const val ARG_CUSTOMER_ID = "customerId"
        private const val ARG_CUSTOMER_NAME = "customerName"
        private const val ARG_BUSINESS_ID = "businessId"
        fun newInstance(
            customerId: String,
            customerName: String,
            businessId: String
        ) = WasooliBottomSheet().apply {
            arguments = Bundle().apply {
                putString(ARG_CUSTOMER_ID, customerId)
                putString(ARG_CUSTOMER_NAME, customerName)
                putString(ARG_BUSINESS_ID, businessId)
            }
        }
    }
}