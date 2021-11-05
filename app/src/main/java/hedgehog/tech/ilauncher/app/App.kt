package hedgehog.tech.ilauncher.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import hedgehog.tech.ilauncher.BuildConfig

class App : Application() {

    companion object{
        private lateinit var appInstance: App
        fun getAppInstance() = appInstance
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        // отключаем темную тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // appmetrica
        val config = YandexMetricaConfig.newConfigBuilder(BuildConfig.AppMetricaKey).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
        // firebase
        FirebaseApp.initializeApp(this)
        FirebaseAnalytics.getInstance(this).logEvent("test", null)
    }

}