package com.bangkit.fraudguard.ui.history

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.databinding.ActivityHistoryDetailBinding
import com.bangkit.fraudguard.databinding.ActivityMainBinding
import com.bangkit.fraudguard.databinding.ActivityProfilePageBinding
import com.bangkit.fraudguard.ui.article.ArticleFragment
import com.bangkit.fraudguard.ui.create.CreateFragment
import com.bangkit.fraudguard.ui.main.MainActivity
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.profile.profilePage.Profile
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding
    private lateinit var viewModel: MainViewModel
    companion object{
        private var ID =""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        checkUserSession()


        ID = intent.getStringExtra("id") ?: ""
        showDetail()
    }

    private fun showDetail(){
        viewModel.getHistoryDetail(ID).observe(this){response->
            if(response.isSuccessful){
                val responseBody = response.body()
                if(responseBody!=null){
                    binding.textLabel.text = responseBody.message
                    if(responseBody.label=="scam"){
                        binding.bottom.setBackgroundResource(R.drawable.list_detail_spam_desc_bar)
                        binding.textDescTop.text = "Spam"
                        binding.textDesc.marginStart
                        binding.textDescTop.setTextColor(Color.parseColor("#C44444"))
                        binding.imageSign.setImageResource(R.drawable.not_secure)
                    }else{
                        binding.bottom.setBackgroundResource(R.drawable.list_detail_desc_bar)
                        binding.textDescTop.text = "Non-Spam"
                        binding.textDescTop.setTextColor(Color.parseColor("#3FC464"))

                    }
                }
            }
        }
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