package com.example.easykhata.utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
fun formatDate(date: Date): String {
    val formattedDate = SimpleDateFormat(
        "d MMM, yy",
        Locale.getDefault()
    ).format(date)
    val day = SimpleDateFormat(
        "d",
        Locale.getDefault()
    ).format(date).toInt()
    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
    return formattedDate.replaceFirst(
        day.toString(),
        "$day$suffix"
    )
}
