package com.bangkit.fraudguard.ui.history

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.databinding.ActivityHistoryDetailBinding
import com.bangkit.fraudguard.ui.main.MainViewModel
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import kotlin.math.round

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
                        binding.textDescTop.text = "Penipuan"
                        binding.textDesc.marginStart
                        binding.textDescTop.setTextColor(Color.parseColor("#C44444"))
                        val roundedPrediction = responseBody.prediction?.let { round(it).toInt() }
                        binding.textDescPercent.text = "${roundedPrediction.toString()}%"
                        binding.textDescPercent.setTextColor(Color.parseColor("#C44444"))
                        binding.imageSign.setImageResource(R.drawable.not_secure)
                        binding.textDesc.text = "Sepertinya teks tersebut merupakan penipuan. Jangan tergiur dengan tawaran yang terlalu bagus untuk menjadi kenyataan. Jika Anda merasa curiga, jangan ragu untuk menghubungi pihak terkait. Anda juga dapat mencari informasi detailnya melalui google atau aplikasi lainnya."
                    }else if(responseBody.label=="normal"){
                        binding.bottom.setBackgroundResource(R.drawable.list_detail_desc_bar)
                        binding.textDescTop.text = "Aman"
                        binding.textDescTop.setTextColor(Color.parseColor("#3FC464"))
                        val roundedPrediction = responseBody.prediction?.let { round(it).toInt() }
                        binding.textDescPercent.text = "${roundedPrediction.toString()}%"
                        binding.imageSign.setImageResource(R.drawable.secure)
                        binding.textDescPercent.setTextColor(Color.parseColor("#3FC464"))
                        binding.textDesc.text = "Sepertinya teks tersebut aman. Namun, Anda tetap harus berhati-hati. Pastikan pengirim pesannya merupakan provider, perusahaan, atau institusi lainnya yang terkenal. Jika Anda merasa curiga, jangan ragu untuk menghubungi pihak terkait."
                    }
                    else if(responseBody.label=="promo"){
                        binding.bottom.setBackgroundResource(R.drawable.list_detail_promo_desc)
                        binding.textDescTop.text = "Promo"
                        binding.textDescTop.setTextColor(Color.parseColor("#C0CB43"))
                        val roundedPrediction = responseBody.prediction?.let { round(it).toInt() }
                        binding.textDescPercent.text = "${roundedPrediction.toString()}%"
                        binding.imageSign.setImageResource(R.drawable.promo_envelope)
                        binding.textDescPercent.setTextColor(Color.parseColor("#C0CB43"))
                        binding.textDesc.text = "Sepertinya teks tersebut merupakan promosi. Meskipun begitu, Anda harus tetap berhati-hati. Pastikan pengirim pesannya merupakan provider, perusahaan, atau institusi lainnya yang terkenal."

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