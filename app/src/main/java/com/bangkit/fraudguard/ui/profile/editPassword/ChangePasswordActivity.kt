package com.bangkit.fraudguard.ui.profile.editPassword

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.data.dto.request.ChangePasswordRequest
import com.bangkit.fraudguard.data.dto.response.ObjectResponse
import com.bangkit.fraudguard.databinding.ActivityChangePasswordBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import org.json.JSONObject
import retrofit2.Response


class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        checkUserSession()
        observeViewModel()

        setupView()
        setupAction()
    }

    private fun checkUserSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }
    private fun observeViewModel() {

    }

    private fun setupView() {
        setMyButtonEnable()
        enableEdgeToEdge()
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

    }

    private fun setupAction() {
        binding.changePasswordButton.setOnClickListener {
            val oldPassword = binding.oldPassword.text.toString()
            val newPasswrod = binding.newPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            val requestDTO = ChangePasswordRequest(oldPassword, newPasswrod, confirmPassword)
            showLoading(true)
            viewModel.changePassword(requestDTO).observe(this) { response ->
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        var userModel = viewModel.getSession().value
                        showLoading(false)
                        showCustomToast(this, "Password berhasil diubah")


                    }
                } else {
                    showLoading(false)
                    var errorMsg = extractErrorMessage(response)
                    showCustomToast(this, errorMsg)

                }
            }

        }
        binding.oldPassword.addTextChangedListener(textWatcher)
        binding.newPassword.addTextChangedListener(textWatcher)
        binding.confirmPassword.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setMyButtonEnable()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun setMyButtonEnable() {
        val oldPasswordValid = binding.oldPassword.text.toString().isNotEmpty()
        val newPasswordValid = binding.newPassword.text.toString().isNotEmpty()
        val confirmPasswordValid = binding.confirmPassword.text.toString().isNotEmpty()

        binding.changePasswordButton.isEnabled = oldPasswordValid && newPasswordValid && confirmPasswordValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this))[MainViewModel::class.java]
    }


    fun goToLoginActivity(context: Context) {
        viewModel.logout()
    }

    private fun extractErrorMessage(response: Response<ObjectResponse>): String {
        return try {
            val json = response.errorBody()?.string()
            val jsonObject = JSONObject(json)
            jsonObject.getString("error")
        } catch (e: Exception) {
            getString(R.string.failed_login_text)
        }
    }
}