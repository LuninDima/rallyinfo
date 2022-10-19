package ru.moondi.rallyinfo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class PushFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "push_channel"
    private val NOTIFICATION_ID = 99


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
        if (!title.isNullOrBlank() && !message.isNullOrBlank()) {
            pushNotification(title, message)
        }
    }

    private fun pushNotification(title: String?, message: String?) {
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(message)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setSound(defaultSoundUri)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notificationManager)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notificationManager: NotificationManager) {
        val name = "pushNotification"
        val descriptionsNotification = "descriptionsNotification"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {

            description = descriptionsNotification

        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
