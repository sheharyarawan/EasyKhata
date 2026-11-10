package com.example.easykhata.utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
private fun getDaySuffix(day: Int): String {
    return when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
}
fun formatDate(date: Date): String {
    val formattedDate = SimpleDateFormat(
        "d MMM, yy",
        Locale.getDefault()
    ).format(date)
    val day = Calendar.getInstance().apply {
        time = date
    }.get(Calendar.DAY_OF_MONTH)
    return formattedDate.replaceFirst(
        day.toString(),
        "$day${getDaySuffix(day)}"
    )
}
fun formatParchaDate(date: Date): String {
    val calendar = Calendar.getInstance().apply {
        time = date
    }
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val time = SimpleDateFormat(
        "h:mm a",
        Locale.getDefault()
    ).format(date).lowercase(Locale.getDefault())
    val formattedDate = SimpleDateFormat(
        "MMM, yyyy",
        Locale.getDefault()
    ).format(date)
    return "$time, $day${getDaySuffix(day)} $formattedDate"
}