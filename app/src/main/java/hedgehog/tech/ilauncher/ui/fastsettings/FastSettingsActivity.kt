package hedgehog.tech.ilauncher.ui.fastsettings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.app.utils.InsetsUtils
import hedgehog.tech.ilauncher.app.utils.PermissionUtils
import hedgehog.tech.ilauncher.app.variables.SCREEN_HEIGHT
import hedgehog.tech.ilauncher.app.variables.SCREEN_WIDTH
import hedgehog.tech.ilauncher.app.variables.SCROLL_TRIGGER
import hedgehog.tech.ilauncher.databinding.FragmentFastSettingsBinding


class FastSettingsActivity: AppCompatActivity() {

    private val viewModel: FastSettingsViewModel by viewModels()
    private lateinit var binding: FragmentFastSettingsBinding
    private lateinit var detector: GestureDetectorCompat

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        binding = FragmentFastSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        detector = GestureDetectorCompat(this, DiaryGestureListener())
        binding.fastSettingsFrame.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
        }

        bindViewsWithViewModel()
        PermissionUtils(this).askRuntimePermissions()
        PermissionUtils(this).getAccessWriteSettings(resultLauncher)
    }

    override fun onDestroy() {
        super.onDestroy()
        SCROLL_TRIGGER = false
    }

    override fun onResume() {
        super.onResume()
        InsetsUtils.hideSystemUI(window, binding.root)
        viewModel.updateWirelessStatuses(this)
        viewModel.updateBatterySaverStatus(this)
        viewModel.updateLocationStatus(this)
    }

    private fun bindViewsWithViewModel() {
        // Air mode
        viewModel.airModeStatusLiveData.observe(this, { status ->
            binding.fsButtonAirMode.isChecked = status
            // If Air mode is on - disable button mobile data
            binding.fsButtonMobileData.isEnabled = !status
        })

        binding.fsButtonAirMode.setOnClickListener {
            viewModel.changeAirModeStatus(this)
        }

        // Mobile data status
        viewModel.mobileDataStatusLiveData.observe(this, { status ->
            binding.fsButtonMobileData.isChecked = status
        })
        binding.fsButtonMobileData.setOnClickListener {
            viewModel.changeMobileDataStatus(this)
        }

        // Wifi
        viewModel.wifiStatusLiveData.observe(this, { status ->
            binding.fsButtonWifi.isChecked = status
        })
        binding.fsButtonWifi.setOnClickListener {
            viewModel.changeWifiStatus(this)
        }

        // Bluetooth status
        viewModel.bluetoothStatusLiveData.observe(this, { status ->
            binding.fsButtonBluetooth.apply {
                isEnabled = true
                isChecked = status
                if (status)
                    background = resources.getDrawable(R.drawable.bg_fs_wifi_bluetooth)
                else
                    background = resources.getDrawable(R.drawable.bg_fs_inactive)
            }
        })
        binding.fsButtonBluetooth.setOnClickListener {
            it.isEnabled = false
            viewModel.changeBluetoothStatus(this)
        }

        // Rotation locking status
        viewModel.rotationLockStatusLiveData.observe(this, { status ->
            binding.fsButtonLockRotation.isChecked = status
        })
        binding.fsButtonLockRotation.setOnClickListener {
            viewModel.changeRotationLockStatus(this)
        }

        // Silent mode status
        viewModel.silentModeStatusLiveData.observe(this, { status ->
            binding.fsButtonSilentMode.isChecked = status
        })
        binding.fsButtonSilentMode.setOnClickListener {
            viewModel.changeSilentModeStatus(this)
        }

        // Brightness
        viewModel.brightnessLiveData.observe(this, { brightness ->
            binding.fsBrightnessSeekbar.progress = brightness
            PermissionUtils(this).getAccessWriteSettings(resultLauncher)
        })
        binding.fsBrightnessSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.setScreenBrightness(this@FastSettingsActivity, progress)
            }
        })

        // Volume
        viewModel.volumeLiveData.observe(this, { volume ->
            binding.fsVolumeSeekbar.progress = volume
            PermissionUtils(this).getAccessWriteSettings(resultLauncher)
        })
        binding.fsVolumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.setVolumeLevel(this@FastSettingsActivity, progress)
            }
        })

        // Flashlight status
        viewModel.flashlightStatusLiveData.observe(this, { status ->
            binding.fsButtonFlashlight.isChecked = status
        })
        binding.fsButtonFlashlight.setOnClickListener {
            viewModel.changeFlashlightStatus(this)
        }

        // Location status
        viewModel.locationStatusLiveData.observe(this, { status ->
            binding.fsButtonLocation.isChecked = status
        })
        binding.fsButtonLocation.setOnClickListener {
            viewModel.changeLocationStatus(this)
        }

        // Battery saver status
        viewModel.batterySaverStatusLiveData.observe(this, { status ->
            binding.fsButtonBatterySaver.isChecked = status
        })
        binding.fsButtonBatterySaver.setOnClickListener {
            viewModel.changeBatterySaverStatus(this)
        }

        // Open home screen
        binding.fsButtonHome.setOnClickListener {
            this.onBackPressed()
        }
        // Open settings
        binding.fsButtonSettings.setOnClickListener {
            viewModel.openSettings(this)
        }
        // Open camera
        binding.faButtonCamera.setOnClickListener {
            viewModel.openCamera(this)
        }
        // Manage alarms
        binding.fsButtonAlarm.setOnClickListener {
            viewModel.openAlarms(this)
        }
        // Open calendar
        binding.fsButtonCalendar.setOnClickListener {
            viewModel.openCalendar(this)
        }
        // Open sound recorder
        binding.fsButtonSoundRecorder.setOnClickListener {
            viewModel.openSoundRecorder(this)
        }



        //================= music box
        

    }

    // ====================================== Gesture ===========================================
    inner class DiaryGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?, // запоминает координаты в месте тапа на всем пути скролла
            e2: MotionEvent?, // передает текущие координаты
            distanceX: Float, // направление и длина скролла по X
            distanceY: Float, // направление и длина скролла по Y
        ): Boolean {
            val needHeight = SCREEN_HEIGHT/2f
            val needWidth = SCREEN_WIDTH/2f
            val xFlag = e2?.x?.compareTo(needWidth)
            val yFlag = e2?.y?.compareTo(needHeight)
            // скролл сверху вниз
            if (distanceY > 0){
                finish()
                // в левой верхней части экрана
                if (xFlag == -1 && yFlag == -1){
                    Log.d("my", "onScroll top left")
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    // ===================================== Permissions =========================================
    var resultLauncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!PermissionUtils(this).checkAccessWriteSettings()){
                ///
            }
        }

}