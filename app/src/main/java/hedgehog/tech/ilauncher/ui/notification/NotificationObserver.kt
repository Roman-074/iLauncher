package hedgehog.tech.ilauncher.ui.notification

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import hedgehog.tech.ilauncher.app.variables.NOTIFICATION_OBSERVER_NAME
import hedgehog.tech.ilauncher.ui.fastsettings.FastSettingsHelper

class NotificationObserver : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName
        val extras = sbn?.notification?.extras

        val titleData =
        if (extras?.getString("android.title") != null) {
            extras.getString("android.title").toString()
        }
        else { "" }

        val textData =
        if (extras?.getCharSequence("android.text") != null) {
            extras.getCharSequence("android.text").toString()
        }
        else { "" }

        val notificationIntent = Intent(NOTIFICATION_OBSERVER_NAME)
        notificationIntent.putExtra("package", packageName)
        notificationIntent.putExtra("title", titleData)
        notificationIntent.putExtra("text", textData)

        // при включенном тихом режиме у нас не должны приходить уведомления
        if (!FastSettingsHelper.getSilentModeStatus(applicationContext)) {
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(notificationIntent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
//        Log.d("my", "Notification remove >>> ")
    }

}