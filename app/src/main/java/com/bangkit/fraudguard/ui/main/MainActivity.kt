package com.bangkit.fraudguard.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.data.adapters.MainHistoryAdapter
import com.bangkit.fraudguard.data.config.ApiServiceSpam
import com.bangkit.fraudguard.data.config.getApiServiceSpam
import com.bangkit.fraudguard.data.dto.response.History
import com.bangkit.fraudguard.data.dto.response.HistoryItem
import com.bangkit.fraudguard.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rvHistory: RecyclerView
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
        rvHistory = findViewById(R.id.rv_history)
        rvHistory.layoutManager = LinearLayoutManager(this)

        val navView: BottomNavigationView = binding.navView
        val itemDecoration = DividerItemDecoration(this, (rvHistory.layoutManager as LinearLayoutManager).orientation)
        rvHistory.addItemDecoration(itemDecoration)
        showHistory()
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_article,
                R.id.navigation_create,
                R.id.navigation_history,
                R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)


    }
    private fun showHistory(){
        val client = getApiServiceSpam("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiaWF0IjoxNzE3NDE5ODg5fQ.Cz3T63Ie2cAeHIvdsjWim6Q9fuDDItPljCqFZMKuC0A").getHistory()
        client.enqueue(object :retrofit2.Callback<History>{
            override fun onResponse(call: Call<History>, response: Response<History>) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        val adapter = MainHistoryAdapter()
                        adapter.submitList(responseBody.response)
                        rvHistory.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<History>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }
}