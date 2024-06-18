package com.bangkit.fraudguard.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.data.dto.request.PredictRequest
import com.bangkit.fraudguard.data.dto.response.PredictResponse
import com.bangkit.fraudguard.databinding.ActivityMainBinding
import com.bangkit.fraudguard.ui.history.HistoryDetailActivity
import com.bangkit.fraudguard.ui.viewModelFactory.ViewModelFactory
import com.bangkit.fraudguard.ui.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var notificationManagerSystem: android.app.NotificationManager
    companion object {
        private const val TAG = "MainActivity"
    }

    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sender = intent.getStringExtra("sender")
            val message = intent.getStringExtra("message")
            predictText(message.toString(), sender.toString(), "SMS")
        }
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
        registerReceiver(smsReceiver, android.content.IntentFilter("SMS_RECEIVED_ACTION"))
        createNotificationChannel()

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

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        unregisterReceiver(smsReceiver)
    }

    private fun sendAlert(title: String, message: String, platform: String, idIntent: String) {
        val notificationId = System.currentTimeMillis().toInt()
        val channelId = "FraudGuard"
        val channelName = "FraudGuard"
        if (idIntent.isNullOrEmpty()) {
            return
        } else if (idIntent == "0") {


            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_splash)
                .setColor(resources.getColor(R.color.secondary))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
            notificationManagerSystem.notify(notificationId, builder.build())

        } else {
            var intent = Intent(this, HistoryDetailActivity::class.java).apply {
                putExtra("id", idIntent)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_splash)
                .setColor(resources.getColor(R.color.secondary))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            notificationManagerSystem.notify(notificationId, builder.build())
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FraudGuard"
            val descriptionText = "FraudGuard Notification Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("FraudGuard", name, importance).apply {
                description = descriptionText
            }
            notificationManagerSystem =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManagerSystem.createNotificationChannel(channel)
        }
    }

    private fun predictText(text: String, sender: String, platform: String) {
        if (text.contains("message") || text.contains("pesan")) {
            return
        }
        val dto = PredictRequest(text)
        viewModel.predict(dto).observe(this)  { response ->

                if (response.isSuccessful) {
                    val predictResponse = response.body()
                    val idDetection = predictResponse?.id
                    val predictionDouble: Double? = predictResponse?.prediction as? Double

                    val formattedPrediction = predictionDouble?.let {
                        String.format("%.2f", it)
                    }
                    var wordPlatform = if (platform == "WhatsApp" || platform == "SMS") {
                        "Pesan"
                    } else if (platform == "Gmail"){
                        "Email"
                    } else {
                        "teks"
                    }

                    if (predictResponse?.label == "penipuan") {
                        var title =
                            "Bahaya !! $wordPlatform dari $sender melalui $platform terindikasi sebagai penipuan"
                        var message =
                            "$wordPlatform dari $sender terindikasi sebagai penipuan dengan confidence-level sebesar $formattedPrediction%"
                        if (idDetection != null) {
                            sendAlert(title, message, platform, idDetection)
                        }
                    } else if (predictResponse?.label == "promo") {
                        var title =
                            "$wordPlatform dari $sender melalui $platform terindikasi sebagai promo dengan confidence-level sebesar $formattedPrediction%"
                        var message =
                            "Tetap hati-hati dengan isi $wordPlatform nya. Pastikan nomornya terdaftar di website/kontak resmi institusi terkait"
                        if (idDetection != null) {
                            sendAlert(title, message, platform, idDetection)
                        }

                    } else if (predictResponse?.label == "normal") {
                        var title = "Kelihatannya $wordPlatform dari $sender aman"
                        var message =
                            "Tidak ada yang mencurigakan dalam $wordPlatform ini. Tetap waspada ya!"
                        if (idDetection != null) {
                            sendAlert(title, message, platform, idDetection)
                        }

                    }

                    var message = "$wordPlatform dari $sender terindikasi sebagai "
                } else {
                    var message = extractErrorMessage(response)
                    if (message.contains("too long")) {
                        message = "Kami tidak bisa mendeteksi teks yang terlalu panjang."
                    } else if (message.contains("array")) {
                        message = "Ada kesalahan dari server kami. Coba lagi nanti."
                    }
                    sendAlert(
                        "Maaf, Kami tidak bisa mendeteksi teks dari $sender melalui $platform",
                        message,
                        platform,
                        "0"
                    )

                }
            }


    }

    private fun extractErrorMessage(response: Response<PredictResponse>): String {
        return try {
            val json = response.errorBody()?.string()
            val jsonObject = JSONObject(json)
            jsonObject.getString("error")
        } catch (e: Exception) {
            "Ada kesalahan dari server kami. Coba lagi nanti."

        }
    }

}