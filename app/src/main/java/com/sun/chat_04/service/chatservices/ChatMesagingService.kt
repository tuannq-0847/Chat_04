package com.sun.chat_04.service.chatservices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sun.chat_04.R

class ChatMesagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        showNotification(message)
        Log.d("test", "From: ${message?.from}")
        Log.d("test", message?.data.toString())
    }

    private fun showNotification(message: RemoteMessage?) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.avatar)
            .setContentTitle(message?.from.toString())
            .setContentText(message?.notification?.body.toString())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val name = getString(R.string.default_notification_channel_id)
            val description = getString(R.string.channel_description)
            val important = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, important)
            channel.description = description
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            val notifcationManager = NotificationManagerCompat.from(this)
            notifcationManager.notify(R.string.default_notification_channel_id, builder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "8080"
    }
}