package com.bangkit.fraudguard.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.databinding.ActivityMainBinding
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    companion object {
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        this.window.setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        checkUserSession()
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_article,
                R.id.navigation_create,
                R.id.navigation_history,
                R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)
        val fragmentToOpen = intent.getStringExtra("fragmentToOpen")
        if (fragmentToOpen == "history") {
            navController.navigate(R.id.navigation_history)
            navView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        if (navController.currentDestination?.id == R.id.navigation_history) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(R.id.navigation_home)
                        }
                        true
                    }
                    else -> {
                        NavigationUI.onNavDestinationSelected(item, navController)
                    }
                }
            }
        } else if (fragmentToOpen == "article") {
            navController.navigate(R.id.navigation_article)
            navView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        if (navController.currentDestination?.id == R.id.navigation_article) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(R.id.navigation_home)
                        }
                        true
                    }
                    else -> {
                        NavigationUI.onNavDestinationSelected(item, navController)
                    }
                }
            }
        } else if (fragmentToOpen == "create") {
            navController.navigate(R.id.navigation_create)
            navView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        if (navController.currentDestination?.id == R.id.navigation_create) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(R.id.navigation_home)
                        }
                        true
                    }
                    else -> {
                        NavigationUI.onNavDestinationSelected(item, navController)
                    }
                }
            }
        } else if (fragmentToOpen == "profile") {
            navController.navigate(R.id.navigation_profile)
            navView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        if (navController.currentDestination?.id == R.id.navigation_profile) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(R.id.navigation_home)
                        }
                        true
                    }
                    else -> {
                        NavigationUI.onNavDestinationSelected(item, navController)
                    }
                }
            }
        }


        setupViewModel()
        checkUserSession()
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        ).get(MainViewModel::class.java)
    }

    private fun checkUserSession() {

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }


}