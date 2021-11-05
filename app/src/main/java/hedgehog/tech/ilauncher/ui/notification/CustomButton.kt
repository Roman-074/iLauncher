package hedgehog.tech.ilauncher.ui.notification

import android.content.Intent
import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import hedgehog.tech.ilauncher.app.App

class CustomButton(
    private val clickListenerOpen: CustomButtonClickListener,
    private val clickListenerManage: CustomButtonClickListener,
    private val clickListenerClear: CustomButtonClickListener
) {

    private var clickRegion: RectF? = null
    private var pos: Int = 0
    private var direction: Int? = null

    fun onDraw(canvasUnder: Canvas, left: Int, top: Int, right: Int, bottom: Int,
        position: Int, layout: View?, direction: Int) {

        canvasUnder.save()

        clickRegion = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        pos = position
        this.direction = direction

        // Apply a clip rectangle with the calculated area to draw inside it
        canvasUnder.clipRect(left, top, right, bottom)

        // Get the custom layout to draw behind the swiped item, if any
        val behindLayoutMain = layout

        if (behindLayoutMain != null) {
            val behindLayoutWidth = right - left
            val behindLayoutHeight = bottom - top

            // Draw the custom layout behind the item
            if (behindLayoutMain.measuredWidth != behindLayoutWidth || behindLayoutMain.measuredHeight != behindLayoutHeight) {
                behindLayoutMain.measure(
                    View.MeasureSpec.makeMeasureSpec(behindLayoutWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(behindLayoutHeight, View.MeasureSpec.EXACTLY)
                )
                behindLayoutMain.setOnClickListener {
                    Log.d("LOG_LOG", "LOG_LOG")
                }
            }

            behindLayoutMain.layout(left, top, right, bottom)
            canvasUnder.save()
            canvasUnder.translate(left.toFloat(), top.toFloat())
            behindLayoutMain.draw(canvasUnder)
        }

        canvasUnder.restore()
    }

    fun onClick(x: Float, y: Float): Boolean {
        clickRegion?.let {
            if (direction == ItemTouchHelper.RIGHT) {
                clickWithRegion(x, y, it) {
                    Log.d("my", "onClick >>>> 1")
                    clickListenerOpen.onClick(pos)
                    openNotification(NotificationRepo.packageNotifList[pos])
                }
            } else {
                val border = (it.right - it.left) / 1.4
//                clickWithRegion(x, y, RectF(it.left, it.top, border.toFloat(), it.bottom)) {
//                    Log.d("my", "onClick >>>> 2")
//                    clickListenerManage.onClick(pos)
//                }
                clickWithRegion(x, y, RectF(border.toFloat(), it.top, it.right, it.bottom)) {
                    Log.d("my", "onClick >>>> 3")
                    clickListenerClear.onClick(pos)
                }
            }
        }

        return false
    }

    private fun clickWithRegion(x: Float, y: Float, region: RectF, action: () -> Unit): Boolean {
        if (region.contains(x, y)) {
            action()
            return true
        }

        return false
    }


    private fun openNotification(packageName: String){
        try {
            val context = App.getAppInstance().applicationContext
            val launchIntent = context?.packageManager?.getLaunchIntentForPackage(packageName)
            context?.startActivity(Intent(launchIntent).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) { }
    }

}