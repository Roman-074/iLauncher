package hedgehog.tech.ilauncher.ui.fastsettings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FastSettingsViewModel(application: Application): AndroidViewModel(application) {

    private val airModeStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val airModeStatusLiveData: LiveData<Boolean> = airModeStatusMutableLiveData

    private val mobileDataStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val mobileDataStatusLiveData: LiveData<Boolean> = mobileDataStatusMutableLiveData

    private val wifiStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val wifiStatusLiveData: LiveData<Boolean> = wifiStatusMutableLiveData

    private val bluetoothStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val bluetoothStatusLiveData: LiveData<Boolean> = bluetoothStatusMutableLiveData

    private val rotationLockStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val rotationLockStatusLiveData: LiveData<Boolean> = rotationLockStatusMutableLiveData

    private val silentModeStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val silentModeStatusLiveData: LiveData<Boolean> = silentModeStatusMutableLiveData

    private val brightnessMutableLiveData: MutableLiveData<Int> = MutableLiveData()
    val brightnessLiveData: LiveData<Int> = brightnessMutableLiveData

    private val volumeMutableLiveData: MutableLiveData<Int> = MutableLiveData()
    val volumeLiveData: LiveData<Int> = volumeMutableLiveData

    private val flashlightStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val flashlightStatusLiveData: LiveData<Boolean> = flashlightStatusMutableLiveData

    private val locationStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val locationStatusLiveData: LiveData<Boolean> = locationStatusMutableLiveData

    private val batterySaverStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val batterySaverStatusLiveData: LiveData<Boolean> = batterySaverStatusMutableLiveData

    init {
        updateWirelessStatuses(application)
        updateRotationLockStatus(application)
        updateSilentModeStatus(application)
        updateScreenBrightness(application)
        updateVolumeLevel(application)
        updateBatterySaverStatus(application)
        updateLocationStatus(application)
    }

    fun updateWirelessStatuses(context: Context) {
        updateAirModeStatus(context)
        updateWifiStatus(context)
        updateMobileDataStatus(context)
        updateBluetoothStatus()
    }


    // ====================================== AirMode ===========================================
    fun changeAirModeStatus(context: Context) {
        FastSettingsHelper.setAirPlaneModeStatus(context)
        updateWirelessStatuses(context)
    }
    private fun updateAirModeStatus(context: Context) {
        val status = FastSettingsHelper.getAirPlaneModeStatus(context)
        airModeStatusMutableLiveData.value = status
    }


    // ================================== Mobile data =========================================
    fun changeMobileDataStatus(context: Context) {
        FastSettingsHelper.setMobileDataStatus(context)
        updateWirelessStatuses(context)
    }
    private fun updateMobileDataStatus(context: Context) {
        val status = FastSettingsHelper.getMobileDataStatus(context)
        mobileDataStatusMutableLiveData.value = status
    }

    // ===================================== Wi-fi ==========================================
    fun changeWifiStatus(context: Context) {
        FastSettingsHelper.setWifiStatus(context)
        updateWirelessStatuses(context)
    }
    private fun updateWifiStatus(context: Context) {
        val newStatus = FastSettingsHelper.getWiFiStatus(context)
        wifiStatusMutableLiveData.value = newStatus
    }

    // ================================== Bluetooth =======================================
    fun changeBluetoothStatus(context: Context) {
        val statusPrev = FastSettingsHelper.getBluetoothStatus()
        FastSettingsHelper.setBluetoothStatus(!statusPrev)
        updateWirelessStatuses(context)
    }
    private fun updateBluetoothStatus() {
// todo для 12 андроида нужно будет запросить пермишен
//        if (PermissionUtils().checkBluetoothAccess()) {
            viewModelScope.launch {
                delay(500)
                val newStatus = FastSettingsHelper.getBluetoothStatus()
                withContext(Dispatchers.Main){
                    bluetoothStatusMutableLiveData.postValue(newStatus)
                }
            }
    }

    // ================================== Rotation lock =======================================
    fun changeRotationLockStatus(context: Context) {
        val statusPrev = FastSettingsHelper.getAutoOrientationStatus(context)
        FastSettingsHelper.setAutoOrientationStatus(context, !statusPrev)
        updateRotationLockStatus(context)
    }
    private fun updateRotationLockStatus(context: Context) {
        rotationLockStatusMutableLiveData.value = !FastSettingsHelper.getAutoOrientationStatus(context)
    }

    // ================================== Silent mode =======================================
    fun changeSilentModeStatus(context: Context) {
        val statusPrev = FastSettingsHelper.getSilentModeStatus(context)
        FastSettingsHelper.setSilentModeStatus(context, !statusPrev)
        updateSilentModeStatus(context)
    }
    private fun updateSilentModeStatus(context: Context) {
        val newStatus = FastSettingsHelper.getSilentModeStatus(context)
        silentModeStatusMutableLiveData.value = newStatus
    }

    // ==================================== Brightness ========================================
    fun setScreenBrightness(context: Context, brightness: Int) {
        ScreenBrightnessHelper.setScreenBrightness(brightness, context)
        brightnessMutableLiveData.value = brightness
    }
    private fun updateScreenBrightness(context: Context) {
        val value = ScreenBrightnessHelper.getScreenBrightness(context)
        brightnessMutableLiveData.value = value
    }

    // ================================== Volume =======================================
    fun setVolumeLevel(context: Context, volume: Int) {
        FastSettingsHelper.setVolumeLevel(context, volume)
        volumeMutableLiveData.value = volume
    }
    private fun updateVolumeLevel(context: Context) {
        val volume = FastSettingsHelper.getVolumeLevel(context)
        volumeMutableLiveData.value = volume
    }

    // ================================== Flashlight =======================================
    fun changeFlashlightStatus(context: Context) {
        val statusPrev = FastSettingsHelper.getFlashLightStatus(context)
        FastSettingsHelper.setFlashLightStatus(context, !statusPrev)
        updateFlashlightStatus(context)
    }
    private fun updateFlashlightStatus(context: Context) {
        viewModelScope.launch {
            delay(100)
            val status = FastSettingsHelper.getFlashLightStatus(context)
            flashlightStatusMutableLiveData.value = status
        }
    }

    // ================================== Location =============================================
    fun changeLocationStatus(context: Context) {
        FastSettingsHelper.setLocationStatus(context)
        updateLocationStatus(context)
    }
    fun updateLocationStatus(context: Context) {
        val status = FastSettingsHelper.getLocationStatus(context)
        locationStatusMutableLiveData.value = status
    }

    // ==================================== Battery =========================================
    fun changeBatterySaverStatus(context: Context) {
        FastSettingsHelper.setBatterySavingModeStatus(context)
        updateSilentModeStatus(context)
    }
    fun updateBatterySaverStatus(context: Context) {
        val status = FastSettingsHelper.getBatterySavingModeStatus(context)
        batterySaverStatusMutableLiveData.value = status
    }

    // ================================== Open Intents =========================================
    fun openSettings(context: Context) {
        FastSettingsHelper.openSettings(context)
    }
    fun openCamera(context: Context) {
        FastSettingsHelper.openCamera(context)
    }
    fun openAlarms(context: Context) {
        FastSettingsHelper.openAlarms(context)
    }
    fun openCalendar(context: Context) {
        FastSettingsHelper.openCalendar(context)
    }
    fun openSoundRecorder(context: Context) {
        FastSettingsHelper.openSoundRecorder(context)
    }

}