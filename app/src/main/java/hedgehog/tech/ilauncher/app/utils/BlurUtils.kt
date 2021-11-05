package hedgehog.tech.ilauncher.app.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import android.R
import android.graphics.Color

/**
        Размытие экрана с помощью библиотечной вьюхи. Размывает РОДИТЕЛЬСКИЙ макет
        
 */

object BlurUtils {

    fun blurView(
        activity: Activity?,
        blurView: BlurView?,
        radius: Float
    ) {
        val decorView = activity?.window?.decorView
        val rootView = decorView?.findViewById<View>(R.id.content) as ViewGroup
        val windowBackground = decorView.background
        blurView?.setupWith(rootView)
            ?.setFrameClearDrawable(windowBackground)
            ?.setBlurAlgorithm(RenderScriptBlur(activity))
            ?.setBlurRadius(radius)
            ?.setBlurAutoUpdate(true)
            ?.setHasFixedTransformationMatrix(false)
            ?.setOverlayColor(Color.parseColor("#30000000"))
    }

}