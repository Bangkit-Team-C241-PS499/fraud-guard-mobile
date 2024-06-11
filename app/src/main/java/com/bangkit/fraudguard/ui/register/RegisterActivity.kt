package com.bangkit.fraudguard.ui.register

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
import com.bangkit.fraudguard.data.dto.request.RegisterRequest
import com.bangkit.fraudguard.data.dto.response.AuthResponse
import com.bangkit.fraudguard.data.model.UserModel
import com.bangkit.fraudguard.databinding.ActivityRegisterBinding
import com.bangkit.fraudguard.ui.alert.showAlert
import com.bangkit.fraudguard.ui.main.MainActivity
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        binding.registerButton.setOnClickListener {
            val name = binding.inputNameRegister.text.toString()
            val email = binding.inputEmailRegister.text.toString()
            val password = binding.inputPasswordRegister.text.toString()
            val confirmPassword = binding.inputConfirmPasswordRegister.text.toString()
            val requestDTO = RegisterRequest(email, name, password, confirmPassword)
            showLoading(true)
            viewModel.register(requestDTO).observe(this) { response ->
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
                            title = "Register berhasil",
                            message = "Selamat datang , ${body.name}!",
                            positiveText = "Lanjut",
                            negativeText = "Kembali",
                            positiveAction = {
                                goToMainActivity(this)
                            },
                            negativeAction = {
                                runBlocking { viewModel.logout() }
                                finish()
                            }
                        )

                    }
                } else {
                    showLoading(false)
                    showAlert(
                        context = this,  // atau requireContext() jika di dalam Fragment
                        title = "Register gagal",
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
        binding.inputEmailRegister.addTextChangedListener(textWatcher)
        binding.inputNameRegister.addTextChangedListener(textWatcher)
        binding.inputPasswordRegister.addTextChangedListener(textWatcher)
        binding.inputConfirmPasswordRegister.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setMyButtonEnable()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun setMyButtonEnable() {
        val nameValid = binding.inputNameRegister.text.toString().isNotEmpty()
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(binding.inputEmailRegister.text.toString()).matches()
        val passwordValid = binding.inputPasswordRegister.text.toString().length >= 8
        val confirmPasswordValid = binding.inputConfirmPasswordRegister.text.toString() == binding.inputPasswordRegister.text.toString() && binding.inputConfirmPasswordRegister.text.toString().isNotEmpty()
        binding.registerButton.isEnabled = nameValid && emailValid && passwordValid && confirmPasswordValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this))[RegisterViewModel::class.java]
    }

    fun goToMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


    private fun extractErrorMessage(response: Response<AuthResponse>): String {
        return try {
            val json = response.errorBody()?.string()
            val jsonObject = JSONObject(json)
            jsonObject.getString("error")
        } catch (e: Exception) {
            getString(R.string.failed_register_text)
        }
    }
}