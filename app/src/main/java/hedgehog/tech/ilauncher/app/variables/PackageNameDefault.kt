package hedgehog.tech.ilauncher.app.variables

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import android.telecom.TelecomManager

/**
        packageName приложений по умолчанию
 */

object PackageNameDefault {

    fun getCamera() : String {
        return "com.android.camera"
    }

    fun getDialer(): String {
        val telecomManager = appContext.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return telecomManager.defaultDialerPackage.toString()
        return " "
    }

    fun getSms() : String {
        return Telephony.Sms.getDefaultSmsPackage(appContext).toString()
    }

    fun getBrowser(): String {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolveInfo = packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        val name = resolveInfo?.activityInfo?.packageName
        return name.toString()
    }

}