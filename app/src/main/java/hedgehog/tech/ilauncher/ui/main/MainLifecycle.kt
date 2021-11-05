package hedgehog.tech.ilauncher.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import hedgehog.tech.ilauncher.app.variables.SCREEN_WIDTH
import hedgehog.tech.ilauncher.customview.topsheet.TopSheetBehavior
import hedgehog.tech.ilauncher.databinding.ActivityMainBinding
import hedgehog.tech.ilauncher.ui.fastsettings.FastSettingsHelper
import hedgehog.tech.ilauncher.ui.notification.*


@SuppressLint("ClickableViewAccessibility")
class MainLifecycle(
    private var binding: ActivityMainBinding,
    private var context: Activity,
    private var lcOwner: LifecycleOwner
) : LifecycleObserver {

    private lateinit var topBarDetector: GestureDetectorCompat
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationAdapter: NotificationAdapter
    private var notificationRepo: NotificationRepo = NotificationRepo()
    private var openNotificationTrigger = false // флаг события перехода на экран уведомлений
    private var notificationState = 0

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun mainOnCreate(){
        // notifications
        topBarDetector = GestureDetectorCompat(context, TopBarGesture())
        binding.homeStatusBar.statusBarFrame.setOnTouchListener { _, event ->
            topBarDetector.onTouchEvent(event)
        }
        notificationViewModel = NotificationViewModel(notificationRepo)
        binding.mainHomeTopSheet.notificationBtnCamera.setOnClickListener {
            FastSettingsHelper.openCamera(context)
        }
        binding.mainHomeTopSheet.notificationBtnLamp.setOnClickListener {
            val statusPrev = FastSettingsHelper.getFlashLightStatus(context)
            FastSettingsHelper.setFlashLightStatus(context, !statusPrev)
        }
//        LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, IntentFilter("notification_message"))
        // fast settings

        Log.d("my", "lifecycle onCreate: MainActivity")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun mainOnDestroy(){
//        LocalBroadcastManager.getInstance(context).unregisterReceiver(onNotice)

        Log.d("my", "lifecycle onDestroy: MainActivity")
    }

    // =================================== TOP SHEET ============================================
    // Gesture
    inner class TopBarGesture : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?, // запоминает координаты в месте тапа на всем пути скролла
            e2: MotionEvent?, // передает текущие координаты
            distanceX: Float, // направление и длина скролла по X
            distanceY: Float, // направление и длина скролла по Y
        ): Boolean {

            val needWidth = SCREEN_WIDTH/2f
            val xFlag = e2?.x?.compareTo(needWidth)
            // в правой части view
//            if (xFlag == 1){
//                if (!openNotificationTrigger){
//                    openNotificationTrigger = true
//                    openTopSheet()
//                }
//            }

            // в левой части view
            if (xFlag == -1){
                if (!openNotificationTrigger){
                    openNotificationTrigger = true
                    openTopSheet()
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    // Swipe down
    private fun openTopSheet() {
        val viewBehavior = TopSheetBehavior.from(binding.mainHomeTopSheet.notificationFrame)
        viewBehavior.state = TopSheetBehavior.STATE_EXPANDED

        setUpRecyclerNotification()
        notificationViewModel.mutableLiveData.observe(lcOwner, Observer { list ->
            notificationAdapter.differ.submitList(list.map { it.copy() })
        })

        viewBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                openNotificationTrigger = false
                // 1 - инициация закрытия, переходное состояние
                // 2 - бросок
                // 3 - шторка коснулась нижней части (экран нотификаций открыт на весь экран)
                // 4 - шторка закрылась
                notificationState = newState
            }
        })

    }

    // For test
    private fun setUpRecyclerNotification() {
        notificationAdapter = NotificationAdapter()

        binding.mainHomeTopSheet.notificationRecycler.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)

            val itemTouchHelperCallback = SwipingItemTouchHelperCallback(
                this,
                notificationAdapter,
                CustomButton(
                    object : CustomButtonClickListener {
                        override fun onClick(pos: Int) {
                            Log.d("onClick", "onClick OPEN position = $pos")
                            notificationViewModel.remove(pos)
                        }
                    },
                    object : CustomButtonClickListener {
                        override fun onClick(pos: Int) {
                            Log.d("onClick", "onClick MANAGE position = $pos")
                            notificationViewModel.remove(pos)
                        }
                    },
                    object : CustomButtonClickListener {
                        override fun onClick(pos: Int) {
                            Log.d("onClick", "onClick CLEAR position = $pos")
                            notificationViewModel.remove(pos)
                        }
                    }
                )
            )
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

}