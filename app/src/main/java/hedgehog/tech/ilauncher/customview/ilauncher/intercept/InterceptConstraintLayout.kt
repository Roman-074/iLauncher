package hedgehog.tech.ilauncher.customview.ilauncher.intercept

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout

class InterceptConstraintLayout  @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet){

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
//        return false
//        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return super.onTouchEvent(event)
        return true
    }

}