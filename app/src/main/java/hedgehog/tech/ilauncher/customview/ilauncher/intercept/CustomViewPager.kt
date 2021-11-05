package hedgehog.tech.ilauncher.customview.ilauncher.intercept

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet
) : ViewPager(context, attributeSet){

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(event)
    }

}