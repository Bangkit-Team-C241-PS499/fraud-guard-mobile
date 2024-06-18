package com.bangkit.fraudguard.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in smsMessages) {
                val sender = message.displayOriginatingAddress
                val messageBody = message.messageBody
                Log.d("sms listener", "onReceive: $sender $messageBody")

                // Create an Intent with action 'SMS_RECEIVED_ACTION'
                val broadcastIntent = Intent("SMS_RECEIVED_ACTION")
                // Add SMS data to the Intent
                broadcastIntent.putExtra("sender", sender)
                broadcastIntent.putExtra("message", messageBody)
                // Send the broadcast
                context.sendBroadcast(broadcastIntent)
            }
        }
    }
}