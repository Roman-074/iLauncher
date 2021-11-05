package hedgehog.tech.ilauncher.customview.ilauncher.intercept

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class InterceptFrameLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet
) : FrameLayout(context, attributeSet){

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
//        return super.onInterceptHoverEvent(event)
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

}