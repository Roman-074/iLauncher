package hedgehog.tech.ilauncher.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.ilauncher.app.variables.LEFT_STOP_COEF_VALUE
import hedgehog.tech.ilauncher.app.variables.RIGHT_STOP_COEF_VALUE

class SwipingItemTouchHelperCallback(
    val recyclerView: RecyclerView,
    private val notificationAdapter: NotificationAdapter,
    val button: CustomButton
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
    private var swipePosition = -1
    private var isSwiping = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, event ->
        if (swipePosition < 0) return@OnTouchListener false
        val point = Point(event.rawX.toInt(), event.rawY.toInt())
        val swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition)
        val swipedItem = swipeViewHolder?.itemView
        val rect = Rect()
        swipedItem?.getGlobalVisibleRect(rect)

        if (event.action == MotionEvent.ACTION_UP) {
            if (rect.top < point.y && rect.bottom > point.y && !isSwiping) {
                Log.d("my", "swipe: $swipePosition")
                button.onClick(event.x, event.y)
                isSwiping = true
            } else {
                swipePosition = -1
            }
        }

        false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipePosition = viewHolder.adapterPosition
        isSwiping = false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition

        if (pos < 0) {
            swipePosition = pos
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX > 0) {
                notificationAdapter.onIsSwipingImpl(
                    (viewHolder as NotificationAdapter.NotificationViewHolder),
                    c,
                    ItemTouchHelper.RIGHT,
                    button
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX * RIGHT_STOP_COEF_VALUE,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            } else {
                notificationAdapter.onIsSwipingImpl(
                    (viewHolder as NotificationAdapter.NotificationViewHolder),
                    c,
                    ItemTouchHelper.LEFT,
                    button
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX * LEFT_STOP_COEF_VALUE,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

    }

    init {
        this.recyclerView.setOnTouchListener(onTouchListener)
    }

}