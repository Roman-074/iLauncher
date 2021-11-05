package hedgehog.tech.ilauncher.app.variables

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import hedgehog.tech.ilauncher.app.App


// ======================================== APP CONST ======================================
val appContext      : Context = App.getAppInstance().applicationContext
val packageManager  : PackageManager = appContext.packageManager
val contentResolver : ContentResolver = appContext.contentResolver
val packageName     : String = appContext.packageName.toString()

// метрики экрана
var SCREEN_HEIGHT = appContext.resources.displayMetrics.heightPixels.toFloat()
var SCREEN_WIDTH = appContext.resources.displayMetrics.widthPixels.toFloat()

// для ресивера перехвата уведомлений
const val NOTIFICATION_OBSERVER_NAME = "notification_message"
// для скрытия панелей навигации на экране онбординга
const val HIDE_SYSTEM_UI_NAME = "hide_ui"

// ======================================== FLAGS ========================================
// для открытия экрана быстрых настроек
var SCROLL_TRIGGER = false
// для хранения позиции выбранной кратинки в онбординге
var PAGE_VP_ON_BOARDING = 0


// ===================================== NOTIFICATIONS =====================================
const val BLUR_VALUE = 20f
const val RIGHT_STOP_COEF_VALUE = 0.35f
const val LEFT_STOP_COEF_VALUE = 0.8f
