package com.bangkit.fraudguard.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in smsMessages) {
                val sender = message.displayOriginatingAddress
                val messageBody = message.messageBody
                // Membuat intent untuk memulai MyNotificationListenerService
                val serviceIntent = Intent(context, MyNotificationListenerService::class.java)
                // Menambahkan pesan SMS dan pengirimnya sebagai data ekstra dalam intent
                serviceIntent.putExtra("sender", sender)
                serviceIntent.putExtra("message", messageBody)
                // Memulai service
                context.startService(serviceIntent)
            }
        }
    }
}