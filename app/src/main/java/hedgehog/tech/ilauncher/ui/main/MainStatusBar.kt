package hedgehog.tech.ilauncher.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.view.View
import android.widget.ImageView
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.app.holders.SingletonHolder
import hedgehog.tech.ilauncher.app.utils.DateUtils
import hedgehog.tech.ilauncher.app.utils.PrefUtils
import hedgehog.tech.ilauncher.app.variables.appContext
import hedgehog.tech.ilauncher.databinding.ActivityMainBinding
import hedgehog.tech.ilauncher.ui.fastsettings.FastSettingsHelper


/**
        Класс для отображения данных в статус-баре, в режиме реального времени
 */

class MainStatusBar private constructor(
    private var binding : ActivityMainBinding
) {

    companion object : SingletonHolder<MainStatusBar, ActivityMainBinding>(::MainStatusBar) { }
    private val receivers = mutableListOf<BroadcastReceiver>()
    private lateinit var batteryStatus : Intent

    // ===================================== INIT ============================================

    fun initStatusBar(){
        registerAllReceivers()
    }

    fun setAllStatus(){
        setTimeStatus()
        setWiFiStatus()
        setLocationStatus()
        setBatteryAfterStart()
        setAutoOrientation()
        setSilentMode()
    }

    private fun registerAllReceivers(){
        // ========================== battery receiver
        val powerFilter = IntentFilter()
        powerFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        powerFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        powerFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, powerFilter)
        // ========================== other
        registerReceiver(timeReceiver,  IntentFilter(Intent.ACTION_TIME_TICK))
        registerReceiver(wifiReceiver,  IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        registerReceiver(locationReceiver,  IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    fun stopActionsStatusBar(){
        unregisterReceiver(batteryReceiver)
        unregisterReceiver(timeReceiver)
        unregisterReceiver(wifiReceiver)
        unregisterReceiver(locationReceiver)
    }

    private fun registerReceiver(receiver: BroadcastReceiver, intentFilter: IntentFilter){
        receivers.add(receiver)
        try {
            // для батареи сделал переменную, чтобы при старте чекать состояние зарядки
            if (receiver == batteryReceiver)
                batteryStatus = appContext.registerReceiver(receiver,  intentFilter)!!
            else
                appContext.registerReceiver(receiver,  intentFilter)
        } catch (e : Exception){ }
    }

    private fun unregisterReceiver(receiver: BroadcastReceiver){
        try {
            if (isRegisteredReceiver(receiver)){
                appContext.unregisterReceiver(receiver)
                receivers.remove(receiver)
            }
        } catch (e : Exception){ }
    }

    private fun isRegisteredReceiver(receiver: BroadcastReceiver): Boolean {
        return receivers.contains(receiver)
    }

    // ===================================== TIME ================================================
    private val timeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action?.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                setTimeStatus()
            }
        }
    }

    private fun setTimeStatus(){
        binding.homeStatusBar.statusBarTimeText.text = DateUtils.getNowTime()
        binding.mainHomeTopSheet.notificationTimeStarted.text = DateUtils.getNowTime()
        binding.mainHomeTopSheet.notificationDateStarted.text = DateUtils.getWeekDay()
    }

    // ===================================== WI-FI ================================================
    private val wifiReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            setWiFiStatus()
        }
    }

    private fun setWiFiStatus(){
        if(FastSettingsHelper.getWiFiStatus(appContext)){
            binding.homeStatusBar.statusBarWifi.visibility = View.VISIBLE
            setWiFiLevel(binding.homeStatusBar.statusBarWifi)
        }
        else binding.homeStatusBar.statusBarWifi.visibility = View.GONE
    }

    private fun setWiFiLevel(icon : ImageView){
        val wifiManager = appContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        /** 3 = хороший сигнал , 4 = отличный сигнал. 5 не бывает */
        val numbersOfLevels = 5
        val wifiInfo = wifiManager.connectionInfo
        when(WifiManager.calculateSignalLevel(wifiInfo.rssi, numbersOfLevels)){
            1 -> icon.setImageResource(R.drawable.ic_white_wifi_enabled_1)
            2 -> icon.setImageResource(R.drawable.ic_white_wifi_enabled_2)
            3 -> icon.setImageResource(R.drawable.ic_white_wifi_enabled_2)
            4 -> icon.setImageResource(R.drawable.ic_white_wifi_enabled)
            else -> icon.setImageResource(R.drawable.ic_white_wifi_enabled_1)
        }
    }

    // ===================================== LOCATION ============================================
    private val locationReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action?.compareTo(LocationManager.PROVIDERS_CHANGED_ACTION) == 0) {
                setLocationStatus()
            }
        }
    }

    private fun setLocationStatus(){
        if (FastSettingsHelper.getLocationStatus(appContext))
            binding.homeStatusBar.statusBarLocation.visibility = View.VISIBLE
        else
            binding.homeStatusBar.statusBarLocation.visibility = View.GONE
    }

    // ===================================== BATTERY ============================================
    private val batteryReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_POWER_CONNECTED){
                PrefUtils.setChargeBattery(true)
            }
            if (intent?.action == Intent.ACTION_POWER_DISCONNECTED){
                PrefUtils.setChargeBattery(false)
            }

            try {
                val value = getBatteryLvL(intent)
                setBatteryText(value)
                selectBatteryImage(value)
            } catch (e : Exception) { }

        }
    }

    private fun selectBatteryImage(value : Int){
        if (PrefUtils.isChargeBattery()){
            if (value == 100) setBatteryImage(R.drawable.ic_battery_green_100)
            if (value in 80..99) setBatteryImage(R.drawable.ic_battery_green_80)
            if (value in 60..79) setBatteryImage(R.drawable.ic_battery_green_60)
            if (value in 40..59) setBatteryImage(R.drawable.ic_battery_green_40)
            if (value in 20..39) setBatteryImage(R.drawable.ic_battery_green_20)
            if (value in 10..19) setBatteryImage(R.drawable.ic_battery_yellow_connect)
            if (value < 10) setBatteryImage(R.drawable.ic_battery_red_connect)
        } else {
            if (value == 100) setBatteryImage(R.drawable.ic_battery_white_100)
            if (value in 80..99) setBatteryImage(R.drawable.ic_battery_white_80)
            if (value in 60..79) setBatteryImage(R.drawable.ic_battery_white_60)
            if (value in 40..59) setBatteryImage(R.drawable.ic_battery_white_40)
            if (value in 20..39) setBatteryImage(R.drawable.ic_battery_white_20)
            if (value in 10..19) setBatteryImage(R.drawable.ic_battery_yellow)
            if (value < 10) setBatteryImage(R.drawable.ic_battery_red)
        }
    }

    // проверяем при старте прилы, заряжается ли батарея
    // функция должна вызываться уже после регистрации ресивера
    private fun setBatteryAfterStart(){
        val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        PrefUtils.setChargeBattery(isCharging)
        val value = getBatteryLvL(batteryStatus)
        selectBatteryImage(value)
        setBatteryText(value)
    }

    private fun getBatteryLvL(intent: Intent?) : Int{
        return try {
            val level: Int? = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int? = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val value: Int = (level!! * 100 / scale?.toFloat()!!).toInt()
            value
        } catch (e : Exception){ 0 }
    }

    private fun setBatteryImage(resource : Int){
        binding.homeStatusBar.statusBarButteryImg.setImageResource(resource)
    }

    private fun setBatteryText(value : Int){
        binding.homeStatusBar.statusBarButteryText.text = "$value%"
    }

    // ================================= AUTO ORIENTATION =====================================
    private fun setAutoOrientation() {
        if(!FastSettingsHelper.getAutoOrientationStatus(appContext))
            binding.homeStatusBar.statusBarBlockOrientation.visibility = View.VISIBLE
        else
            binding.homeStatusBar.statusBarBlockOrientation.visibility = View.GONE
    }

    // ================================= Silent Mode ========================================
    private fun setSilentMode() {
        if(FastSettingsHelper.getSilentModeStatus(appContext))
            binding.homeStatusBar.statusBarSoundMode.visibility = View.VISIBLE
        else
            binding.homeStatusBar.statusBarSoundMode.visibility = View.GONE
    }

    //todo доработать: установка конкретных значений при вызове метода setAllStatus
}