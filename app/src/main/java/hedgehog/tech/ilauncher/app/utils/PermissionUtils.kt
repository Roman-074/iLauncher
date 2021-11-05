package hedgehog.tech.ilauncher.app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.provider.Settings
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.app.holders.SingletonHolder
import hedgehog.tech.ilauncher.app.variables.HIDE_SYSTEM_UI_NAME
import hedgehog.tech.ilauncher.app.variables.appContext
import hedgehog.tech.ilauncher.app.variables.contentResolver
import hedgehog.tech.ilauncher.app.variables.packageName


class PermissionUtils(
    private val context: Activity
) {

    companion object : SingletonHolder<PermissionUtils, Activity>(::PermissionUtils) {
        const val PERMISSION_REQUEST_CODE = 666
    }

    private val perms = arrayOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.ACCESS_COARSE_LOCATION, // для включения местоположения
        Manifest.permission.CAMERA // для включения фонарика
    )

// todo  для Android 12
//    @RequiresApi(Build.VERSION_CODES.S)
//    private val android12perms = arrayOf(
//        Manifest.permission.BLUETOOTH_CONNECT
//    )

    // -1 = разрешение не предоставлено
    // 0 = разрешение предоставлено
    fun askRuntimePermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                perms.forEach {
                    val result = context.checkSelfPermission(it)
                    if (result == -1) {
                        ActivityCompat.requestPermissions(context, perms, PERMISSION_REQUEST_CODE)
                        return@forEach
                    }
                }
            }

// todo  для Android 12
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                android12perms.forEach {
//                    val result = context.checkSelfPermission(it)
//                    if (result == -1) {
//                        ActivityCompat.requestPermissions(
//                            context,
//                            android12perms,
//                            PERMISSION_REQUEST_CODE
//                        )
//                        return@forEach
//                    }
//                }
//            }
        } catch (e: Exception) { }
    }

    private fun createPermissionDialog(layout: Int): Dialog? {
        if (context != null) {
            val dialog = Dialog(context)
            dialog.setContentView(layout)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setOnDismissListener(permissionListener)
            dialog.show()
            return dialog
        }
        return null
    }

    private val permissionListener = DialogInterface.OnDismissListener {
        // для скрытия панелей навигации на экране онбординга
        val notificationIntent = Intent(HIDE_SYSTEM_UI_NAME)
        notificationIntent.putExtra("dismiss", "dismiss")
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(notificationIntent)
    }

    // =================================== Notifications =========================================
    fun checkAccessNotification(): Boolean {
        val notificationListenerString =
            Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return !(notificationListenerString == null || !notificationListenerString.contains(
            packageName
        ));
    }

    // Если нет доступа на перехват уведомлений, то запрашиваем его
    fun getAccessNotification(resultLauncher: ActivityResultLauncher<Intent>) {
        if (!checkAccessNotification()) {
            callDialogNotifications(resultLauncher)
        }
    }

    @SuppressLint("NewApi")
    private fun callDialogNotifications(resultLauncher: ActivityResultLauncher<Intent>) {
        try {
            val dialog = createPermissionDialog(R.layout.dialog_permission_notifications)
            dialog?.findViewById<Button>(R.id.permission_notification_btn_no)?.setOnClickListener {
                dialog.dismiss()
            }
            dialog?.findViewById<Button>(R.id.permission_notification_btn_yes)?.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                resultLauncher.launch(intent)
            }
        } catch (e: Exception) {
        }
    }

    // =================================== Manage Settings ========================================
    @SuppressLint("NewApi")
    fun checkAccessWriteSettings(): Boolean {
        return Settings.System.canWrite(context)
    }

    // Если нет доступа на редактирование системных настроек, то запрашиваем его
    fun getAccessWriteSettings(resultLauncher: ActivityResultLauncher<Intent>) {
        if (!checkAccessWriteSettings()) {
            callDialogManageSettings(resultLauncher)
        }
    }

    @SuppressLint("NewApi")
    private fun callDialogManageSettings(resultLauncher: ActivityResultLauncher<Intent>) {
        try {
            val dialogView = createPermissionDialog(R.layout.dialog_permission_write_settings)
            dialogView?.findViewById<Button>(R.id.permission_write_settings_button_cancel)
                ?.setOnClickListener {
                    dialogView.dismiss()
                }
            dialogView?.findViewById<Button>(R.id.permission_write_settings_button_ok)
                ?.setOnClickListener {
                    dialogView.dismiss()
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                    resultLauncher.launch(intent)
                }
        } catch (e: Exception) {
        }
    }

    // ====================================== Overlay ===========================================
    @SuppressLint("NewApi")
    fun checkAccessOverlay(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    // Если нет доступа на наложение поверх окон, то запрашиваем его
    fun getAccessOverlay(resultLauncher: ActivityResultLauncher<Intent>) {
        if (!checkAccessOverlay()) {
            callDialogOverlay(resultLauncher)
        }
    }

    @SuppressLint("NewApi")
    private fun callDialogOverlay(resultLauncher: ActivityResultLauncher<Intent>) {
        try {
            val dialogView = createPermissionDialog(R.layout.dialog_permission_overlay)
            dialogView?.findViewById<Button>(R.id.permission_overlay_button_cancel)
                ?.setOnClickListener {
                    dialogView.dismiss()
                }
            dialogView?.findViewById<Button>(R.id.permission_overlay_button_ok)
                ?.setOnClickListener {
                    dialogView.dismiss()
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    resultLauncher.launch(intent)
                }
        } catch (e: Exception) {
        }
    }

// todo  для Android 12
    // ============================== Bluetooth access (API S) ====================================
//    fun checkBluetoothAccess() : Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val checkPermission = App.getAppInstance().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
//            Log.d("my", "checkBluetoothAccess >> $checkPermission ")
//            if (checkPermission == -1) return false
//            if (checkPermission == 0) return true
//        }
//
//        return true
//    }

}