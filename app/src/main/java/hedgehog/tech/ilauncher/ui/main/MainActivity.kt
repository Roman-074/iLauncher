package hedgehog.tech.ilauncher.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.pwittchen.swipe.library.rx2.Swipe
import hedgehog.tech.ilauncher.app.utils.InsetsUtils
import hedgehog.tech.ilauncher.app.variables.NOTIFICATION_OBSERVER_NAME
import hedgehog.tech.ilauncher.databinding.ActivityMainBinding
import hedgehog.tech.ilauncher.ui.home.HomeFragment
import hedgehog.tech.ilauncher.ui.notification.NotificationRepo
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar


class MainActivity : AppCompatActivity() {

    private lateinit var mainLifecycle: MainLifecycle
    private lateinit var binding: ActivityMainBinding
    private lateinit var swipe: Swipe
    private lateinit var unRegistrar: Unregistrar
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainLifecycle = MainLifecycle(binding, this, this)
        lifecycle.addObserver(mainLifecycle)

        setHelpGuideClickListener()

        binding.mainViewpager.apply {
            adapter = GlobalVPAdapter(viewModel, supportFragmentManager, binding)
            setPageTransformer(false, GlobalVPTransformer())
            addOnPageChangeListener(MainListeners(binding, this@MainActivity).pageListenerVP)
            currentItem = 1
        }

        // костыль
        HomeFragment.binding?.homeViewpager?.visibility = View.GONE

        // перехват ивентов появления клавиатуры
        unRegistrar = KeyboardVisibilityEvent.registerEventListener(
            this, MainListeners(binding, this).keyBoardListener
        )
        // глобальные перехваты тачей
        swipe = Swipe()
        swipe.setListener(MainListeners(binding, this).swipeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mainLifecycle)
        binding.mainViewpager.removeOnPageChangeListener(
            MainListeners(
                binding,
                this
            ).pageListenerVP
        )
        unRegistrar.unregister()
    }

    override fun onStart() {
        super.onStart()
        MainStatusBar.getInstance(binding).initStatusBar()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            notificationBroadcast, IntentFilter(NOTIFICATION_OBSERVER_NAME)
        )
    }

    override fun onStop() {
        super.onStop()
        MainStatusBar.getInstance(binding).stopActionsStatusBar()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationBroadcast)
    }

    override fun onResume() {
        super.onResume()
        InsetsUtils.hideSystemUI(window, binding.root)
        MainStatusBar.getInstance(binding).setAllStatus()
    }

    override fun onBackPressed() {
        // не позволяем выйти из приложения
    }

    // для глобального перехвата тачей
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        swipe.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }


    // ================================ START GUIDE HELP =========================================
    private fun setHelpGuideClickListener() {
        swipeNext(binding.mainHelpLayout.helpSwipe1, binding.mainHelpLayout.helpSwipe2)
        swipeNext(binding.mainHelpLayout.helpSwipe2, binding.mainHelpLayout.helpSwipe3)
        swipeNext(binding.mainHelpLayout.helpSwipe3, binding.mainHelpLayout.helpSwipe4)
        binding.mainHelpLayout.helpSwipe4.setOnClickListener {
            binding.mainHelpLayout.root.visibility = View.GONE
        }
    }

    private fun swipeNext(view1: View, view2: View) {
        view1.setOnClickListener {
            view1.visibility = View.GONE
            view2.visibility = View.VISIBLE
        }
    }


    // ================================ NOTIFICATIONS =========================================
    private val notificationBroadcast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val packageName = p1?.getStringExtra("package")
            val titleData = p1?.getStringExtra("title")
            val textData = p1?.getStringExtra("text")
            if (packageName != null && titleData != null && textData != null) {
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                    .setPackage(packageName)
                val packageInfo = packageManager.queryIntentActivities(mainIntent, 0)
                if (packageInfo.size > 0){
                    val icon = packageInfo[0].loadIcon(packageManager)
                    val appName = packageInfo[0].loadLabel(packageManager).toString()
                    NotificationRepo.addNotification(
                        packageName, titleData, textData,
                        appName, icon
                    )
                }
            }
        }
    }

}
