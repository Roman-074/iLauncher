package hedgehog.tech.ilauncher.ui.fastsettings

import android.bluetooth.BluetoothAdapter
import android.content.*
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import hedgehog.tech.ilauncher.app.App.Companion.getAppInstance
import hedgehog.tech.ilauncher.app.utils.ToastUtils
import hedgehog.tech.ilauncher.app.variables.PackageNameDefault
import hedgehog.tech.ilauncher.app.variables.appContext
import hedgehog.tech.ilauncher.app.variables.packageManager
import java.lang.reflect.Method
import java.util.concurrent.Executor


object FastSettingsHelper {

    // ====================================== Wi-Fi ===========================================
    fun setWifiStatus(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val i = Intent(Settings.Panel.ACTION_WIFI)
            context.startActivity(i)
        } else {
            val wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            wifiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(wifiIntent)
        }
    }

    fun getWiFiStatus(context: Context): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED
    }

    // ====================================== AirMode ===========================================
    // !!!Установка AirMode программно доступна только на рутованом устройстве
    // Поэтому делаем проброс в настройки
    fun setAirPlaneModeStatus(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                val airIntent = Intent("android.settings.WIRELESS_SETTINGS")
                airIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(airIntent)
            } catch (ex: ActivityNotFoundException) {
                ToastUtils.showShortToast("Not able set airplane")
            }
        }
    }

    fun getAirPlaneModeStatus(context: Context): Boolean {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON,
            0
        ) != 0
    }

    // =================================== Auto Orientation ======================================

    fun setAutoOrientationStatus(context: Context, status: Boolean) {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            if (status) 1 else 0
        )
    }
    fun getAutoOrientationStatus(context: Context): Boolean {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            0
        ) != 0
    }


    // ====================================== Mobile Data =======================================
    // !!!Установка передачи данных программно доступна только на рутованом устройстве
    // Поэтому делаем проброс в настройки
    fun setMobileDataStatus(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val i = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            context.startActivity(i)
        } else {
            val dataIntent = Intent()
            dataIntent.component = ComponentName(
                "com.android.settings",
                "com.android.settings.Settings\$DataUsageSummaryActivity"
            )
            dataIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(dataIntent)
        }
    }

    fun getMobileDataStatus(context: Context): Boolean {
        if(getAirPlaneModeStatus(context)) return false

        var mobileDataEnabled = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            val cmClass = Class.forName(cm.javaClass.name)
            val method: Method = cmClass.getDeclaredMethod("getMobileDataEnabled")
            method.isAccessible = true
            mobileDataEnabled = method.invoke(cm) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mobileDataEnabled
    }

    // ====================================== Bluetooth =========================================
    fun setBluetoothStatus(status: Boolean) {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return
        mBluetoothAdapter.apply {
            if(status) enable() else disable()
        }
    }

    fun getBluetoothStatus(): Boolean {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
        return mBluetoothAdapter.isEnabled
    }

    // ===================================== Silent Mode =========================================
    fun setSilentModeStatus(context: Context, status: Boolean) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = if (status) AudioManager.RINGER_MODE_SILENT else AudioManager.RINGER_MODE_NORMAL
    }
    fun getSilentModeStatus(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.ringerMode == AudioManager.RINGER_MODE_SILENT
    }

    // ====================================== Volume LvL =========================================
    fun setVolumeLevel(context: Context, value: Int) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(AudioManager.STREAM_RING, value, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getVolumeLevel(context: Context): Int {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamVolume(AudioManager.STREAM_RING)
    }

    // =============================== Battery saving mode ==================================
    fun setBatterySavingModeStatus(context: Context) {
        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
        context.startActivity(intent)
    }
    fun getBatterySavingModeStatus(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode
    }

    // ====================================== FlashLight =======================================
    fun setFlashLightStatus(context: Context, status: Boolean) {
        val cameraProcessFuture = ProcessCameraProvider.getInstance(context)
        cameraProcessFuture.addListener({
            val cameraProvider = cameraProcessFuture.get()
            cameraProvider.unbindAll()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .apply {
                    val executor = Executor { Runnable {} }
                    setAnalyzer(executor, {
                        it.close()
                    })
                }
            val camera = cameraProvider.bindToLifecycle(
                (context as AppCompatActivity),
                cameraSelector,
                imageAnalysis
            )
            val cameraControl = camera.cameraControl
            cameraControl.enableTorch(status)
        }, ContextCompat.getMainExecutor(context))
    }

    fun getFlashLightStatus(context: Context): Boolean {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val camera = cameraProvider.bindToLifecycle(
            context as AppCompatActivity, cameraSelector
        )
        val status = camera.cameraInfo.torchState.value
        //cameraProvider.unbindAll()
        return status == TorchState.ON
    }

    // ====================================== Location =========================================
    fun setLocationStatus(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }  catch (ex: Exception){
            ToastUtils.showShortToast("Error set location status")
        }
    }

    fun getLocationStatus(context: Context): Boolean {
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return LocationManagerCompat.isLocationEnabled(lm)
        } catch (ex: Exception){
            ToastUtils.showShortToast("Error get location status")
        }
        return false
    }



    // =================================== Open Intent ===========================================
    fun openSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            context.startActivity(intent)
        } catch (ex: Exception){
            ToastUtils.showShortToast("Not found settings")
        }
    }

    fun openCamera(context: Context) {
        try {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            context.startActivity(intent)
        } catch (ex: Exception){
            ToastUtils.showShortToast("Not found camera")
        }
    }

    fun openAlarms(context: Context) {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
            context.startActivity(intent)
        }  catch (ex: Exception){
            ToastUtils.showShortToast("Not found alarms")
        }
    }

    fun openCalendar(context: Context) {
        try {
            val startMillis: Long = System.currentTimeMillis()
            val builder: Uri.Builder = CalendarContract.CONTENT_URI.buildUpon()
                .appendPath("time")
            ContentUris.appendId(builder, startMillis)
            val intent = Intent(Intent.ACTION_VIEW).setData(builder.build())
            context.startActivity(intent)
        } catch (ex: Exception){
            ToastUtils.showShortToast("Not found calendar")
        }
    }

    fun openSoundRecorder(context: Context) {
        try {
            context.startActivity(Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION))
        } catch (e: Exception) {
            ToastUtils.showShortToast("Not found recorder")
        }
    }

    fun openCaller(){
        try {
            getAppInstance().applicationContext.startActivity(
                Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e : Exception) {
            ToastUtils.showShortToast("Not found dial")
        }
    }

    fun openMessage(){
        try {
            getAppInstance().applicationContext.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "", null))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e : Exception) {
            ToastUtils.showShortToast("Not found sms")
        }
    }

    fun openSafari(){
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(PackageNameDefault.getBrowser())
            appContext.startActivity(launchIntent)
        }
        catch (e : Exception){
            ToastUtils.showShortToast("Not found browser")
        }
    }

}