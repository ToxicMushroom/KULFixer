package me.melijn.kulfixer

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


class KulNotifService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // This method will be called when a new notification arrives
        Log.d("NotificationListener", "Notification Posted: " + sbn.packageName)
        if (sbn.packageName == "be.kuleuven.icts.authenticator") {
            // Perform actions based on the notification here
            sbn.notification.contentIntent.send()

            return
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // This method will be called when a notification is removed
        Log.d("NotificationListener", "Notification Removed: " + sbn.packageName)
    }
}