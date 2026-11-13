package com.example.easykhata.utils
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.example.easykhata.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
fun Fragment.showConfirmationDialog(
    title: String,
    message: String,
    positiveButtonText: String = "DELETE",
    negativeButtonText: String = "CANCEL",
    onPositiveClick: () -> Unit
) {
    val dialog = MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { _, _ ->
            onPositiveClick()
        }
        .setNegativeButton(negativeButtonText, null)
        .create()

    dialog.setOnShowListener {
        dialog.window?.setBackgroundDrawableResource(android.R.color.white)
        val titleView = dialog.findViewById<TextView>(
            com.google.android.material.R.id.alertTitle
        )
        titleView?.apply {
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
        }
        dialog.findViewById<TextView>(
            android.R.id.message
        )?.apply {
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.BLACK)
            textSize = 18f
        }
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.red)
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            Color.BLACK
        )
    }
    dialog.show()
}