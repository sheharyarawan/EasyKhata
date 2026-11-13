package com.example.easykhata.utils
import androidx.fragment.app.Fragment
import com.example.easykhata.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
fun Fragment.showPastDatePicker(
    onDateSelected: (Date) -> Unit
) {
    val today = MaterialDatePicker.todayInUtcMilliseconds()
    val constraints = CalendarConstraints.Builder()
        .setEnd(today)
        .setValidator(DateValidatorPointBackward.before(today))
        .build()
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setSelection(today)
        .setCalendarConstraints(constraints)
        .setTheme(R.style.ThemeOverlay_EasyKhata_DatePicker)
        .build()
    datePicker.addOnPositiveButtonClickListener { selection ->
        val selectedDate = Calendar.getInstance(
            TimeZone.getTimeZone("UTC")
        ).apply {
            timeInMillis = selection
        }
        val updatedDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
            set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
        }
        onDateSelected(Date(updatedDate.timeInMillis))
    }
    datePicker.show(parentFragmentManager, "DATE_PICKER")
}
