package com.bangkit.fraudguard.ui.customView

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.bangkit.fraudguard.R

fun showCustomToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
    val inflater = LayoutInflater.from(context)
    val layout = inflater.inflate(R.layout.custom_toast, null)

    val text: TextView = layout.findViewById(R.id.toast_text)
    text.text = message

    val toast = Toast(context)
    toast.duration = duration
    toast.view = layout
    toast.show()
}
