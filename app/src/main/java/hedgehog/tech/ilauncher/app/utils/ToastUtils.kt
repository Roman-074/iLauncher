package hedgehog.tech.ilauncher.app.utils

import android.widget.Toast
import hedgehog.tech.ilauncher.app.App

object ToastUtils {

    private val allToasts: ArrayList<Toast> = arrayListOf()
    private fun killAllToast() {
        allToasts.forEach { it.cancel() }
        allToasts.clear()
    }

    fun showShortToast(message : String){
        killAllToast()
        val newToast = Toast.makeText(
            App.getAppInstance().applicationContext,
            message,
            Toast.LENGTH_SHORT
        )
        allToasts.add(newToast)
        newToast.show()
    }

    fun showLongToast(message : String){
        killAllToast()
        val newToast = Toast.makeText(
            App.getAppInstance().applicationContext,
            message,
            Toast.LENGTH_LONG
        )
        allToasts.add(newToast)
        newToast.show()
    }

}