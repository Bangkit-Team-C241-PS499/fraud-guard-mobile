package com.bangkit.fraudguard.ui.profile.detailProfile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.data.dto.response.ProfileResponse
import com.bangkit.fraudguard.databinding.ActivityDetailProfileBinding
import com.bangkit.fraudguard.ui.customView.showCustomToast
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.profile.editPassword.ChangePasswordActivity
import com.bangkit.fraudguard.ui.profile.editProfile.EditProfileActivity
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bumptech.glide.Glide

class DetailProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProfileBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        observeProfile()
        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.btnEditProfile.setOnClickListener {
            var intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)

        }

        binding.btnChangePhoto.setOnClickListener {


        }

        binding.changePasswordCard.setOnClickListener {
            var intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)


        }
    }

    private fun observeProfile() {
        viewModel.showProfile().observe(this, Observer { response ->
            if (response.isSuccessful) {
                showCustomToast(this, "Success to load profile")
                val profile: ProfileResponse? = response.body()
                // Update UI dengan data profile
                binding.profileEmail.text = profile?.email
                binding.tvDescriptionEmail.text = profile?.email
                binding.profileName.text = profile?.name
                binding.tvDescriptionName.text = profile?.name

                if (profile?.photoUrl != null) {
                    Glide.with(this).load(profile.photoUrl).into(binding.profileImage)
                }
            } else {
                showCustomToast(this, "Failed to load profile", Toast.LENGTH_SHORT)
            }
        })
    }

    private fun setupView() {
        enableEdgeToEdge()
        setContentView(binding.root)
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this))[MainViewModel::class.java]
    }
}