package com.bangkit.fraudguard.ui.profile.editProfile

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.data.dto.request.UpdateProfileRequest
import com.bangkit.fraudguard.data.dto.response.ObjectResponse
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import com.bangkit.fraudguard.databinding.ActivityEditProfileBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.profile.editPassword.ChangePasswordActivity
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Response


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        observeViewModel()

        setupView()
        setupAction()
    }

    private fun observeViewModel() {
        showLoading(true)
        viewModel.showProfile().observe(this, Observer { response ->
            try {
                if (response.isSuccessful) {
                    showLoading(false)
                    val profile: ProfileResponse? = response.body()
                    binding.nameInput.setText(profile?.name)
                    binding.emailInput.setText(profile?.email)
                } else {
                    showLoading(false)
                    showCustomToast(this, "Tidak bisa menampilkan profile")
                }
            } catch (e: Exception) {
                showLoading(false)
                showCustomToast(this, e.message.toString())
            }
        })


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
        binding.editProfileButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val requestDTO = UpdateProfileRequest(name)
            showLoading(true)
            viewModel.updateProfile(requestDTO).observe(this) { response ->
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        var userModel = viewModel.getSession().value
                        viewModel.getSession().value?.let { userModel ->
                            userModel.name = name
                            runBlocking { viewModel.saveSession(userModel) }
                        }
                        showLoading(false)
                        showCustomToast(this, "Nama berhasil diubah")
                        Thread.sleep(2000)
                        finish()
                    }
                } else {
                    showLoading(false)
                    var errorMsg = extractErrorMessage(response)
                    showCustomToast(this, errorMsg)

                }
            }

        }
        binding.nameInput.addTextChangedListener(textWatcher)

        binding.gantiPasswordButton.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setMyButtonEnable()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun setMyButtonEnable() {
        val nameValid = binding.nameInput.text.toString().isNotEmpty()

        binding.editProfileButton.isEnabled = nameValid
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