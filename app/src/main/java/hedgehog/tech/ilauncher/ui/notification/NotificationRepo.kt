package hedgehog.tech.ilauncher.ui.notification

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import hedgehog.tech.ilauncher.data.model.Notification

class NotificationRepo {

    companion object{
        var mutableLiveData: MutableLiveData<List<Notification>> = MutableLiveData()
        private var notifications : MutableList<Notification> = mutableListOf()
        private var notificationID = 0
        var packageNotifList: MutableList<String> = mutableListOf() // для кликов по нотификациям

        // Создаем уведомление для отрисовки
        fun addNotification(packageName: String,
                            title: String,
                            text: String,
                            appName : String,
                            iconNotificationApp : Drawable){

            val record = Notification(notificationID, appName, iconNotificationApp, title, text, "")
            try {
                notifications.add(notificationID, record)
                packageNotifList.add(packageName)
            } catch (e : Exception) { }

            // TODO id'шники могут быть заданы некорректно при удалении уведомлений из списка
            notificationID++
            mutableLiveData.postValue(notifications)
        }

    }

    fun remove(position: Int) {
        notifications.removeAt(position)
        mutableLiveData.postValue(notifications)
    }

}