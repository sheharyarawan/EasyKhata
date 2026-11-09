package com.example.easykhata.utils
object PhoneNumberUtils {
    fun normalize(phone: String): String {
        val digits = phone.filter(Char::isDigit)
        return when {
            digits.startsWith("92") && digits.length == 12 ->
                "0${digits.drop(2)}"
            digits.startsWith("0092") && digits.length == 14 ->
                "0${digits.drop(4)}"
            digits.startsWith("03") && digits.length == 11 ->
                digits
            else -> digits
        }
    }
    fun isValidLocalNumber(phone: String): Boolean {
        return phone.length == 11 &&
                phone.startsWith("03") &&
                phone.all(Char::isDigit)
    }
}