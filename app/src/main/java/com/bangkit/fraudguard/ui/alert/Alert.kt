package com.bangkit.fraudguard.ui.alert

import android.content.Context
import androidx.appcompat.app.AlertDialog



fun showAlert(
    context: Context,
    title: String,
    message: String,
    positiveText : String,
    negativeText : String,
    positiveAction: () -> Unit = { },
    negativeAction: () -> Unit = { }
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(positiveText) { dialog, which ->
        positiveAction()
        dialog.dismiss()
    }
    builder.setNegativeButton(negativeText) { dialog, which ->
        negativeAction()
        dialog.dismiss()
    }
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

