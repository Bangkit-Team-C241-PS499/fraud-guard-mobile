package com.bangkit.fraudguard.ui.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bangkit.fraudguard.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var clearButtonImage: Drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
    private var inputType: String? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomEditText,
            0, 0
        ).apply {
            try {
                inputType = getString(R.styleable.CustomEditText_inputType)
            } finally {
                recycle()
            }
        }
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateInput(input: String) {
        when (inputType) {
            "email" -> {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    setError(context.getString(R.string.invalid_email), null)
                }
            }

            "password" -> {
                if (input.length < 8) {
                    setError(context.getString(R.string.invalid_password), null)
                }
            }

            "confirm_password" -> {
                // Membandingkan input dengan newPassword
                val parent = parent as ViewGroup
                val newPasswordEditText = parent.findViewWithTag<CustomEditText>("new_password")
                val newPassword = newPasswordEditText.text.toString()

                if (newPassword != input) {
                    setError(context.getString(R.string.password_mismatch), null)
                }
            }

            "name" -> {
                if (input.isEmpty()) {
                    setError(context.getString(R.string.invalid_name), null)
                }
            }

            else -> {
                setError(null, null)
            }
        }
    }
}
