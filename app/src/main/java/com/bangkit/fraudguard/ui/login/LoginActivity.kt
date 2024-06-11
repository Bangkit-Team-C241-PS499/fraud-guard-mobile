package com.bangkit.fraudguard.ui.login

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
import com.bangkit.fraudguard.data.dto.request.LoginRequest
import com.bangkit.fraudguard.data.dto.response.AuthResponse
import com.bangkit.fraudguard.data.model.UserModel
import com.bangkit.fraudguard.databinding.ActivityLoginBinding
import com.bangkit.fraudguard.ui.alert.showAlert
import com.bangkit.fraudguard.ui.main.MainActivity
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        setupView()
        setupAction()
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
        binding.loginButton.setOnClickListener {
            val email = binding.inputLoginEmail.text.toString()
            val password = binding.inputLoginPassword.text.toString()
            val requestDTO = LoginRequest(email, password)
            showLoading(true)
            viewModel.login(requestDTO).observe(this) { response ->
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val userModel = UserModel(
                            email,
                            body.name,
                            body.userId,
                            body.token,
                            true
                        )
                        runBlocking { viewModel.saveSession(userModel) }
                        showLoading(false)
                        showAlert(
                            context = this,  // atau requireContext() jika di dalam Fragment
                            title = "Login berhasil",
                            message = "Selamat datang kembali, ${body.name}!",
                            positiveText = "Lanjut",
                            negativeText = "Ganti Akun",
                            positiveAction = {
                                goToMainActivity(this)
                            },
                            negativeAction = {
                                goToLoginActivity(this)
                            }
                        )

                    }
                } else {
                    showLoading(false)
                    showAlert(
                        context = this,  // atau requireContext() jika di dalam Fragment
                        title = "Login gagal",
                        message = extractErrorMessage(response),
                        positiveText = "Coba lagi",
                        positiveAction = {
                            // do nothing
                        },
                        negativeText = "Batal",
                        negativeAction = {
                            // do nothing
                        }
                    )

                }
            }

        }
        binding.inputLoginEmail.addTextChangedListener(textWatcher)
        binding.inputLoginPassword.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setMyButtonEnable()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun setMyButtonEnable() {
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(binding.inputLoginEmail.text.toString()).matches()
        val passwordValid = binding.inputLoginPassword.text.toString().length >= 8
        binding.loginButton.isEnabled = emailValid && passwordValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this))[LoginViewModel::class.java]
    }

    fun goToMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    fun goToLoginActivity(context: Context) {
        viewModel.logout()
    }

    private fun extractErrorMessage(response: Response<AuthResponse>): String {
        return try {
            val json = response.errorBody()?.string()
            val jsonObject = JSONObject(json)
            jsonObject.getString("error")
        } catch (e: Exception) {
            getString(R.string.failed_login_text)
        }
    }
}