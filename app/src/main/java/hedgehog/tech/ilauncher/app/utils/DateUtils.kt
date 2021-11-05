package hedgehog.tech.ilauncher.app.utils

import android.annotation.SuppressLint
import android.os.Build
import hedgehog.tech.ilauncher.app.App
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object DateUtils {

    // текущее время: часы минуты
    fun getNowTime() : String {
        val calendar: Calendar = Calendar.getInstance()
        val hours: Int = Integer.valueOf(SimpleDateFormat("HH").format(calendar.time))
        val minutes: Int = Integer.valueOf(SimpleDateFormat("mm").format(calendar.time))
        val hoursString = if(hours < 10) "0$hours" else "$hours"
        val minutesString = if(minutes < 10) "0$minutes" else "$minutes"
        return "$hoursString:$minutesString"
    }

    // текущее число месяца и день недели
    fun getWeekDay() : String {
        val context = App.getAppInstance().applicationContext
        val calendar = Calendar.getInstance()
        // =================== день недели ===================
        val sdf = SimpleDateFormat("EEEE")
        val dayOfTheWeek = sdf.format(calendar.time).replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase(Locale.getDefault())
            else
                it.toString()
        }

        // ====================== число ======================
        val numberOfWeek = calendar[Calendar.DAY_OF_MONTH]

        // ====================== месяц ======================
        val locale : Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
        val localeCode = locale.country.lowercase()
        val month = SimpleDateFormat("MMMM", Locale(localeCode)).format(calendar.time)

        return "$dayOfTheWeek, $numberOfWeek $month"
    }

}