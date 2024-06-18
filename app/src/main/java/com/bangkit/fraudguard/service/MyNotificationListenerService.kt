package com.bangkit.fraudguard.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bangkit.fraudguard.R
import com.bangkit.fraudguard.data.config.ApiServiceSpam
import com.bangkit.fraudguard.data.config.getApiServiceSpam
import com.bangkit.fraudguard.data.dto.request.PredictRequest
import com.bangkit.fraudguard.data.dto.response.PredictResponse
import com.bangkit.fraudguard.data.preferences.UserPreference
import com.bangkit.fraudguard.data.preferences.dataStore
import com.bangkit.fraudguard.ui.history.HistoryDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class MyNotificationListenerService : NotificationListenerService() {

    private lateinit var pref: UserPreference
    private lateinit var apiService: ApiServiceSpam
    private lateinit var notificationManagerSystem: NotificationManager

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch(Dispatchers.Main) {
            getUserToken()
        }
        createNotificationChannel()

    }

    private suspend fun getUserToken() {
        pref = UserPreference.getInstance(this@MyNotificationListenerService.dataStore)
        try {
            val token = pref.getSession().first().token
            apiService = getApiServiceSpam(token)
        } catch (e: Exception) {
            Log.e("NotificationListener", "Error getting user token")
        }
    }



    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        sbn?.let { notification ->
            if (notification.packageName == "com.whatsapp") {
                val extras = notification.notification.extras
                val title = extras.getString("android.title")
                val text = extras.getCharSequence("android.text").toString()
                if (title?.contains("08") == true || title?.contains("628") == true) {
                    if (title?.contains("message") == true || title?.contains("pesan") == true) {
                        println("Pesan dari $title")
                    } else {
                        predictText(text, title, "WhatsApp")

                    }
                } else {
                    println("Pesan dari $title")

                }
            } else if (notification.packageName == "com.google.android.gm") {
                val extras = notification.notification.extras
                val title = extras.getString("android.title")


                val text = extras.getCharSequence("android.text").toString()
                val message = extras.getCharSequence("android.bigText").toString()
                val textLines = extras.getCharSequenceArray("android.textLines")
                val fullMessage = textLines?.joinToString("\n") ?: message

                if (title?.contains("message") == true || title?.contains("pesan") == true) {
                    println("Pesan dari $title")
                } else {
                    if (title != null ){
                        predictText(message, title, "Gmail")

                    } else{
                        println("pesan dari $title")
                    }

                }


            } else if (notification.packageName == "com.android.mms" || notification.packageName == "com.google.android.apps.messaging" || notification.packageName == "com.android.messaging" || notification.packageName == "com.samsung.android.messaging" || notification.packageName == "com.google.android.apps.nbu.messaging" || notification.packageName == "com.google.android.apps.messaging" || notification.packageName == "com.google.android.talk" || notification.packageName == "com.google.android.apps.googlevoice") {
                Log.d("Gabing", "SMS detected")
                val extras = notification.notification.extras
                val title = extras.getString("android.title")
                val text = extras.getCharSequence("android.text").toString()
                Log.d("Gabing", "SMS detected $text")

                if (title?.contains("message") == true || title?.contains("pesan") == true) {
                    println("Pesan dari $title")
                } else {
                    if (title != null ){
                        predictText(text, title, "WhatsApp")

                    } else{
                        return
                    }

                }

            }
            else{
                Log.d("cek sms", notification.notification.extras.toString())

            }
        }
    }

    private fun getAppName(packageName: String): String {
        val packageManager = applicationContext.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
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
        apiService.predict(dto).enqueue(object : retrofit2.Callback<PredictResponse> {
            override fun onResponse(
                call: Call<PredictResponse>,
                response: Response<PredictResponse>
            ) {
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

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                sendAlert(
                    "Maaf, Kami tidak bisa mendeteksi pesan dari $sender melalui $platform",
                    "Ada kesalahan dari server kami. Coba lagi nanti.",
                    platform,
                    "0"
                )
            }
        })
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
