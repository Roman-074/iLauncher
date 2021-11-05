package hedgehog.tech.ilauncher.ui.fastsettings

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import kotlin.math.E
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

object ScreenBrightnessHelper {

    // Input level range in 0..100
    fun setScreenBrightness(linearLevel: Int, context: Context) {

        val logarithmic = when(Build.MANUFACTURER) {
            "HUAWEI" -> huaweiLinearToLogarithmic(linearLevel)
            "Xiaomi" -> xiaomiLinearToLogarithmic(linearLevel)
            else -> standardLinearToLogarithmic(linearLevel)
        }

        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                logarithmic
            )

            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )

            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                logarithmic
            )
        } catch (e: Exception) {
            Log.e("Screen Brightness", "error changing screen brightness")
        }
    }

    fun getScreenBrightness(context: Context): Int {
        return try {
            val logarithmicLevel= Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)

            val linearLevel = when(Build.MANUFACTURER) {
                "HUAWEI" -> huaweiLogarithmicToLinear(logarithmicLevel)
                "Xiaomi" -> xiaomiLogarithmicToLinear(logarithmicLevel)
                else -> standardLogarithmicToLinear(logarithmicLevel)
            }
            return if (linearLevel < 0) 0 else linearLevel

        } catch (e: Exception) {
            100
        }
    }

    /*  Reverse engineered formula for logarithmic scale is y=19.811*ln(x)-9.411
     *  where input value range X is 0..250 and output Y is 0..100
     *  Bellow is reverted formula
    */
    private fun standardLinearToLogarithmic(linearLevel: Int): Int {
        return (E.pow((linearLevel+9.411)/19.811)).toInt()
    }
    private fun standardLogarithmicToLinear(logarithmicLevel: Int): Int {
        return ceil(19.811*ln(logarithmicLevel.toFloat())-9.411).toInt()
    }

    // Another logarithmic function for Huawei phones
    private fun huaweiLinearToLogarithmic(linearLevel: Int): Int {
        return ((E.pow((linearLevel+183.4)/50)) - 39).toInt()
    }
    private fun huaweiLogarithmicToLinear(logarithmicLevel: Int): Int {
        return ceil(ln(logarithmicLevel.toFloat()+39)*50 - 183.4).toInt()
    }

    // For Xiaomi
    private fun xiaomiLinearToLogarithmic(linearLevel: Int): Int {
        return (((E.pow((linearLevel+124.3)/40)) - 22)/250 * 4095).toInt()
    }
    private fun xiaomiLogarithmicToLinear(logarithmicLevel: Int): Int {
        return ceil(ln((logarithmicLevel.toFloat()/4095)*250 + 22)*40 - 124.3).toInt()
    }

}
