package hedgehog.tech.ilauncher.app.utils

import android.content.Context
import android.content.SharedPreferences
import hedgehog.tech.ilauncher.app.App

object PrefUtils {

    // ====================================== BOOLEAN ===========================================
    // первое открытие приложения
    fun isFirstOpenApp(): Boolean{ return sharedPreferences.getBoolean("first_open", true) }
    fun setFirstOpenApp(open : Boolean){ spEditor.putBoolean("first_open", open).apply() }

    // текущий статус зарядки телефона (заряжается/не заряжается)
    fun isChargeBattery() : Boolean{ return sharedPreferences.getBoolean("charge_battery", false) }
    fun setChargeBattery(open : Boolean){ spEditor.putBoolean("charge_battery", open).apply() }


    // ================================= SHARED PREFERENCES =====================================
    private val sharedPreferences = App.getAppInstance().getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
    private val spEditor: SharedPreferences.Editor = sharedPreferences.edit()

}
