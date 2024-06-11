package com.bangkit.fraudguard.ui.welcome

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.fraudguard.databinding.ActivityWelcomeBinding
import com.bangkit.fraudguard.ui.login.LoginActivity
import com.bangkit.fraudguard.ui.register.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    private val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)  // Gunakan binding.root di sini
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupAction()
    }

    private fun setupAction() {
        binding.LoginButton.setOnClickListener {  // Gunakan loginButton dengan huruf kecil
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.RegisterButton.setOnClickListener {  // Gunakan registerButton dengan huruf kecil
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
