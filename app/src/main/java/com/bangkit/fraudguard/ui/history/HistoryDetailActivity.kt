package com.bangkit.fraudguard.ui.history

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.databinding.ActivityHistoryDetailBinding
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity

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
                    if(responseBody.label=="penipuan"){
                        binding.bottom.setBackgroundResource(R.drawable.list_detail_spam_desc_bar)
                        binding.textDescTop.text = "Spam"
                        binding.textDesc.marginStart
                        binding.textDescTop.setTextColor(Color.parseColor("#C44444"))
                        val decimalValue = responseBody.prediction as? Double ?: 0.0
                        val percentValue = decimalValue // Jika prediction sudah dalam skala 0-100
                        binding.textDescPercent.text = "%.0f%%".format(percentValue)
                        binding.textDescPercent.setTextColor(Color.parseColor("#C44444"))
                        binding.imageSign.setImageResource(R.drawable.not_secure)
                    }else if(responseBody.label=="normal"){
                        binding.bottom.setBackgroundResource(R.drawable.list_detail_desc_bar)
                        binding.textDescTop.text = "Non-Spam"
                        binding.textDescTop.setTextColor(Color.parseColor("#3FC464"))
                        val decimalValue = responseBody.prediction as? Double ?: 0.0
                        val percentValue = decimalValue // Jika prediction sudah dalam skala 0-100
                        binding.textDescPercent.text = "%.0f%%".format(percentValue)
                    }
                    else if(responseBody.label=="promo"){
                        binding.bottom.setBackgroundResource(R.drawable.list_detail_desc_bar)
                        binding.textDescTop.text = "Promo"
                        binding.textDescTop.setTextColor(Color.parseColor("#3FC464"))
                        val decimalValue = responseBody.prediction as? Double ?: 0.0
                        val percentValue = decimalValue // Jika prediction sudah dalam skala 0-100
                        binding.textDescPercent.text = "%.0f%%".format(percentValue)
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