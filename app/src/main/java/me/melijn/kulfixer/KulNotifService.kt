package me.melijn.kulfixer

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class KulNotifService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // This method will be called when a new notification arrives
        Log.d("NotificationListener", "Notification Posted: " + sbn.packageName)
        if (sbn.packageName == "be.kuleuven.icts.authenticator") {
            val intent = Intent(
                Intent.ACTION_VIEW,
                // KUL authenticator opens for these uri's
                Uri.parse("https://icts.kuleuven.be/apps/authenticator/")
            ).apply {
                // Required flag since we're not inside an activity in this service
                this.addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            return
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // This method will be called when a notification is removed
        Log.d("NotificationListener", "Notification Removed: " + sbn.packageName)
    }
}