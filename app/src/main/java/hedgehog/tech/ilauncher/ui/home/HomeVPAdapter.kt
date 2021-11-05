package hedgehog.tech.ilauncher.ui.home

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.AdapterView
import androidx.core.view.GestureDetectorCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.yandex.metrica.YandexMetrica
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.app.variables.SCREEN_HEIGHT
import hedgehog.tech.ilauncher.app.variables.SCREEN_WIDTH
import hedgehog.tech.ilauncher.app.variables.SCROLL_TRIGGER
import hedgehog.tech.ilauncher.data.model.GridItemModel
import hedgehog.tech.ilauncher.ui.fastsettings.FastSettingsActivity
import kotlinx.android.synthetic.main.view_home_grid_item_in_vp.view.*


class HomeVPAdapter(
        private var viewPagerContext: Context?,
        private var viewPagerSize: Int,
        private var gridAppList: MutableList<MutableList<GridItemModel>>,
): PagerAdapter() {

    private lateinit var detector: GestureDetectorCompat
    override fun getCount(): Int = viewPagerSize

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(viewPagerContext).inflate(R.layout.view_home_grid_item_in_vp, null)

//        view.home_grid_view.apply {
//            val customAdapter = DragHomeItemAdapter(gridAppList[position])
//            val lm = GridLayoutManager(viewPagerContext, 4)
//            layoutManager = lm
//            adapter = customAdapter
//            setOnDragListener(customAdapter.dragHomeItemInstance)
//        }

        val adapter = HomeVPAppGridAdapter(viewPagerContext, 0, gridAppList[position])
        view.home_grid_view.adapter = adapter
        view.home_grid_view.onItemClickListener = AppClickListener(viewPagerContext, gridAppList[position])

        // свайпы/флинги на гриде view pager'a
        detector = GestureDetectorCompat(viewPagerContext, DiaryGestureListener())
        view.home_grid_view.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
        }

        (container as ViewPager).addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }



    //  ================================== OTHER CLASSES ========================================
    class AppClickListener(
            private var context: Context?,
            private var appList: MutableList<GridItemModel>
    ): AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val launchIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val cp = ComponentName(appList[position].pName, appList[position].initActivity)
            launchIntent.component = cp
            context?.startActivity(launchIntent)
            YandexMetrica.reportEvent("App click: ${appList[position].appName}")
        }
    }

//    class LongAppClickListener: AdapterView.OnItemLongClickListener{
//        override fun onItemLongClick(
//            parent: AdapterView<*>?, view: View?, position: Int, id: Long
//        ): Boolean {
//            val data = ClipData.newPlainText("", "")
//            val shadowBuilder = View.DragShadowBuilder(view)
//            view?.startDrag(data, shadowBuilder, view, 0)
//            view?.visibility = View.INVISIBLE
//            return true
//        }
//    }


    inner class DiaryGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            try {
                val needHeight = SCREEN_HEIGHT/2f
                val needWidth = SCREEN_WIDTH/2f
                val xFlag = e2?.x?.compareTo(needWidth)
                val yFlag = e2?.y?.compareTo(needHeight)
                // скролл сверху вниз
                if (velocityY > 0){
                    // в правой верхней части экрана
                    if (xFlag == 1 && yFlag == -1){
                        if (!SCROLL_TRIGGER){
                            SCROLL_TRIGGER = true
                            openFastSettings()
                        }
                    }
                    // в левой верхней части экрана
                    if (xFlag == -1 && yFlag == -1){ }
                }
            }
            catch (ex: Exception){}

            return super.onFling(e1, e2, velocityX, velocityY)
        }

    }

    private fun openFastSettings(){
        viewPagerContext?.startActivity(Intent(viewPagerContext, FastSettingsActivity::class.java))
    }

}