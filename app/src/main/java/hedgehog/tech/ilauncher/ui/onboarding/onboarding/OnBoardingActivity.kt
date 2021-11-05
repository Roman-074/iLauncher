package hedgehog.tech.ilauncher.ui.onboarding.onboarding

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.app.utils.InsetsUtils
import hedgehog.tech.ilauncher.app.utils.PermissionUtils
import hedgehog.tech.ilauncher.app.utils.PermissionUtils.Companion.PERMISSION_REQUEST_CODE
import hedgehog.tech.ilauncher.app.variables.HIDE_SYSTEM_UI_NAME
import hedgehog.tech.ilauncher.app.variables.PAGE_VP_ON_BOARDING
import hedgehog.tech.ilauncher.databinding.ActivityOnboardingBinding
import hedgehog.tech.ilauncher.ui.main.MainActivity


class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.onboardingButton.setOnClickListener {
            if (!PermissionUtils(this).checkAccessNotification()) {
                getNotification()
                return@setOnClickListener
            } else if (!PermissionUtils(this).checkAccessWriteSettings()) {
                getWriteSettings()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        setImage()
        PermissionUtils(this).askRuntimePermissions()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(dismissDialogBroadcast, IntentFilter(HIDE_SYSTEM_UI_NAME))
    }

    override fun onResume() {
        super.onResume()
        Log.d("my", "onResume: >>> ")
        InsetsUtils.hideSystemUI(window, binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dismissDialogBroadcast)
    }

    // ================================ SYSTEM PERMISSIONS ====================================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    grantResults.forEachIndexed { index, i ->
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            //
                        }
                    }
                }
            }
        }
    }

    // ================================ OTHER PERMISSIONS ====================================
    private var resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (!PermissionUtils(this).checkAccessOverlay()){
//                getOverlay()
//                return@registerForActivityResult
//            }
            if (!PermissionUtils(this).checkAccessNotification()) {
                getNotification()
                return@registerForActivityResult
            }
            if (!PermissionUtils(this).checkAccessWriteSettings()) {
                getWriteSettings()
            }
        }


    // =================================== FUNCTIONS ========================================
// todo отключил до тех пор, пока не будет готова кнопка с меню
//    private fun getOverlay(){
//        binding.onboardingTopText.text = resources.getString(R.string.onboarding_overlay_top_text)
//        PermissionUtils(this).getAccessOverlay(resultLauncher)
//    }

    private fun getWriteSettings() {
        binding.onboardingTopText.text =
            resources.getString(R.string.onboarding_top_text_write_settings)
        PermissionUtils(this).getAccessWriteSettings(resultLauncher)
    }

    private fun getNotification() {
        binding.onboardingTopText.text =
            resources.getString(R.string.onboarding_top_text_notification)
        PermissionUtils(this).getAccessNotification(resultLauncher)
    }


    @SuppressLint("ResourceType")
    private fun setImage() {
        val wm = WallpaperManager.getInstance(applicationContext)
        when (PAGE_VP_ON_BOARDING) {
            0 -> {
                binding.onboardingImage.setImageResource(R.drawable.bg_workspace_0)
                wm.setResource(R.drawable.bg_workspace_0)
            }
            1 -> {
                binding.onboardingImage.setImageResource(R.drawable.bg_workspace_1)
                wm.setResource(R.drawable.bg_workspace_1)
            }
            2 -> {
                binding.onboardingImage.setImageResource(R.drawable.bg_workspace_2)
                wm.setResource(R.drawable.bg_workspace_2)
            }
            // если пользователь захотел оставить текущую картинку рабочего стола
            666 -> {
                binding.onboardingImage.setImageResource(R.drawable.bg_workspace_default)
            }
        }
    }

    // для  скрывания системных панелей после показа диалога
    private val dismissDialogBroadcast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val dismiss = p1?.getStringExtra("dismiss")
            if (dismiss != null) {
                InsetsUtils.hideSystemUI(window, binding.root)
            }
        }
    }

}