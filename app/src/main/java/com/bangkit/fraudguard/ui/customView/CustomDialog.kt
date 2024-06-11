package com.bangkit.fraudguard.ui.customView

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bangkit.fraudguard.R

fun Context.showCustomAlertDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    val dialogView = LayoutInflater.from(this).inflate(androidx.core.R.layout.custom_dialog, null)
    val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)

    val dialog = dialogBuilder.create()

    val dialogIcon = dialogView.findViewById<ImageView>(R.id.dialog_icon)
    val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
    val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
    val positiveButton = dialogView.findViewById<Button>(R.id.dialog_positive_button)
    val negativeButton = dialogView.findViewById<Button>(R.id.dialog_negative_button)



    if (dialogTitle != null) {
        dialogTitle.text = title
    } else {
        Log.e("CustomAlertDialog", "Dialog title is null")
    }

    if (dialogMessage != null) {
        dialogMessage.text = message
    } else {
        Log.e("CustomAlertDialog", "Dialog message is null")
    }

    if (positiveButton != null) {
        positiveButton.text = positiveButtonText
        positiveButton.setOnClickListener {
            onPositiveButtonClick()
            dialog.dismiss()
        }
    } else {
        Log.e("CustomAlertDialog", "Positive button is null")
    }

    if (negativeButton != null) {
        negativeButton.text = negativeButtonText
        negativeButton.setOnClickListener {
            onNegativeButtonClick()
            dialog.dismiss()
        }
    } else {
        Log.e("CustomAlertDialog", "Negative button is null")
    }

    dialog.show()
}
