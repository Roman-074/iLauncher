package hedgehog.tech.ilauncher.ui.notification

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import eightbitlab.com.blurview.RenderScriptBlur
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.app.utils.DateUtils
import hedgehog.tech.ilauncher.app.variables.BLUR_VALUE
import hedgehog.tech.ilauncher.data.model.Notification
import hedgehog.tech.ilauncher.databinding.ViewRecyclerNotificationItemBinding

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    lateinit var rect: Rect

    inner class NotificationViewHolder(val binding: ViewRecyclerNotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal var behindSwipedItemLayout: View? = null
        internal var secondaryBehindSwipedItemLayout: View? = null
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.NotificationViewHolder {

        val binding = ViewRecyclerNotificationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val decorView = (parent.context as Activity).window.decorView
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        val windowBackground = decorView.background

        binding.notificationItemBlur.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(parent.context))
            .setBlurRadius(BLUR_VALUE)

        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        rect = Rect(
            holder.itemView.left,
            holder.itemView.top,
            holder.itemView.right,
            holder.itemView.bottom
        )

        holder.apply {
            differ.currentList[position].let { notification ->
                binding.apply {
//                    notificationItemImgAppIcon.setImageResource(notification.icon)
                    notificationItemImgAppIcon.setImageDrawable(notification.icon)
                    notificationItemTxtAppTitle.text = notification.title
                    notificationItemTxtAction.text = notification.action
                    notificationItemTxtMainInfo.text = notification.mainInfo
                    notificationItemTxtAdditionalInfo.text = notification.additionalInfo
                    notificationItemTxtTime.text = DateUtils.getNowTime()


                    itemView.setOnClickListener {
                        onItemClickListener?.let {
                            it(notification)
                        }
                    }

                }

                behindSwipedItemLayout = getBehindSwipedItemLayout(holder)
                secondaryBehindSwipedItemLayout = getSecondaryBehindSwipedItemLayout(holder)
            }
        }
    }

    private var onItemClickListener: ((Notification) -> Unit)? = null

    fun setOnItemClickListener(listener: (Notification) -> Unit) {
        onItemClickListener = listener
    }

    private fun getBehindSwipedItemLayoutId(): Int = R.layout.view_notification_item_swipe_bg

    private fun getBehindSwipedItemLayout(viewHolder: NotificationViewHolder): View? {
        if (viewHolder.behindSwipedItemLayout?.id != getBehindSwipedItemLayoutId()) {
            // The behind-swiped-items layout was never inflated before or the
            // layout ID has changed, so we inflate it
            return recyclerView.context?.let { context ->
                LayoutInflater
                    .from(context)
                    .inflate(getBehindSwipedItemLayoutId(), null, false)
            }
        }

        // The behind-swiped layout was already inflated, so we just return it
        return viewHolder.behindSwipedItemLayout
    }

    private fun getSecondaryBehindSwipedItemLayoutId(): Int =
        R.layout.view_notification_item_swipe_bg_secondary

    private fun getSecondaryBehindSwipedItemLayout(viewHolder: NotificationViewHolder): View? {

        if (viewHolder.secondaryBehindSwipedItemLayout?.id != getSecondaryBehindSwipedItemLayoutId()) {
            // The behind-swiped-items layout was never inflated before or the
            // layout ID has changed, so we inflate it
            return recyclerView.context?.let { context ->
                LayoutInflater
                    .from(context)
                    .inflate(getSecondaryBehindSwipedItemLayoutId(), null, false)
            }
        }

        // The behind-swiped layout was already inflated, so we just return it
        return viewHolder.behindSwipedItemLayout
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    private fun drawOnSwiping(
        viewHolder: NotificationViewHolder,
        canvasUnder: Canvas?,
        direction: Int,
        button: CustomButton
    ) {

        recyclerView.let {
            var layout: View? = null

            var newLeft = 0
            var newTop = 0
            var newRight = 0
            var newBottom = 0

            // The original "coordinates" of the layout from which it is being moved away
            val originalLayoutAreaLeft = viewHolder.itemView.left /*recyclerView.left*/
            val originalLayoutAreaTop = viewHolder.itemView.top
            val originalLayoutAreaRight = viewHolder.itemView.right /*recyclerView.right*/
            val originalLayoutAreaBottom = viewHolder.itemView.bottom

            // The current "coordinates" of the layout with the swipe translation applied to it
            val currentLayoutAreaLeft =
                viewHolder.itemView.left + viewHolder.itemView.translationX.toInt()
            val currentLayoutAreaRight =
                viewHolder.itemView.right + viewHolder.itemView.translationX.toInt()

            if (direction == ItemTouchHelper.LEFT) {
                newLeft = currentLayoutAreaRight
                newTop = originalLayoutAreaTop
                newRight = originalLayoutAreaRight
                newBottom = originalLayoutAreaBottom
                layout = LayoutInflater
                    .from(viewHolder.itemView.context)
                    .inflate(R.layout.view_notification_item_swipe_bg, null, false)
            } else {
                newLeft = originalLayoutAreaLeft
                newTop = originalLayoutAreaTop
                newRight = currentLayoutAreaLeft
                newBottom = originalLayoutAreaBottom

                layout = LayoutInflater
                    .from(viewHolder.itemView.context)
                    .inflate(R.layout.view_notification_item_swipe_bg_secondary, null, false)
            }

            if (canvasUnder != null)
                button.onDraw(
                    canvasUnder,
                    newLeft,
                    newTop,
                    newRight,
                    newBottom,
                    viewHolder.adapterPosition,
                    layout,
                    direction
                )

        }
    }

    fun onIsSwipingImpl(
        viewHolder: NotificationViewHolder,
        canvasUnder: Canvas?,
        direction: Int,
        button: CustomButton
    ) {
        drawOnSwiping(viewHolder, canvasUnder, direction, button)
    }

    override fun getItemCount(): Int = differ.currentList.size
}
