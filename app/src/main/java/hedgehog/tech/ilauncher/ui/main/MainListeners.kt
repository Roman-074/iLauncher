package hedgehog.tech.ilauncher.ui.main

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.github.pwittchen.swipe.library.rx2.SwipeListener
import hedgehog.tech.ilauncher.app.utils.InsetsUtils
import hedgehog.tech.ilauncher.databinding.ActivityMainBinding
import hedgehog.tech.ilauncher.ui.home.HomeFragment
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


class MainListeners(
    private val binding : ActivityMainBinding,
    private val activity: Activity
) {

    // ================================ Page Listener =========================================
    var pageListenerVP = object: ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (position == 0){
                if (positionOffset > 0f)
                    HomeFragment.binding?.homeViewpager?.visibility = View.VISIBLE
                if (positionOffset == 0.0f)
                    HomeFragment.binding?.homeViewpager?.visibility = View.GONE
            }
        }
        override fun onPageScrollStateChanged(state: Int) { }
        override fun onPageSelected(position: Int) { }
    }

    // ============================= Swipe Listener =========================================
    val swipeListener = object : SwipeListener {
        override fun onSwipingLeft(event: MotionEvent?) {
            InsetsUtils.hideKeyboardOnActivity(activity)
        }
        override fun onSwipedLeft(event: MotionEvent?): Boolean {
            return false
        }
        override fun onSwipingRight(event: MotionEvent?) { }
        override fun onSwipedRight(event: MotionEvent?): Boolean {
            return false
        }
        override fun onSwipingUp(event: MotionEvent?) { }
        override fun onSwipedUp(event: MotionEvent?): Boolean {
            return false
        }
        override fun onSwipingDown(event: MotionEvent?) {
            InsetsUtils.hideKeyboardOnActivity(activity)
        }
        override fun onSwipedDown(event: MotionEvent?): Boolean {
            return false
        }
    }

    // ============================= KeyBoard Listener =========================================
    val keyBoardListener = object : KeyboardVisibilityEventListener {
        override fun onVisibilityChanged(isOpen: Boolean) {
            if (isOpen)
                InsetsUtils.showSystemUI(activity.window, binding.root)
            else
                InsetsUtils.hideSystemUI(activity.window, binding.root)
        }
    }


}