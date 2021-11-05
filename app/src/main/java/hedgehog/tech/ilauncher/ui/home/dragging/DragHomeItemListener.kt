package hedgehog.tech.ilauncher.ui.home.dragging

import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.data.model.GridItemModel

class DragHomeItemListener : View.OnDragListener {

    private var isDropped = false

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                dropRecycler(v, event)
            }
        }

        if (!isDropped && event.localState != null) {
            (event.localState as View).visibility = View.VISIBLE
        }

        return true
    }

    private fun dropRecycler(v: View, event: DragEvent){
        isDropped = true
        var positionTarget = -1
        val viewSource = event.localState as View?
        val viewId = v.id
        val recyclerView1 = R.id.home_app_bar
        val recyclerView2 = R.id.home_grid_view///
        when (viewId) {
            recyclerView1, recyclerView2 -> {
                val target: RecyclerView
                when (viewId) {
                    // добавление в нижнее меню
                    recyclerView1 -> {
                        target = v.rootView.findViewById<View>(recyclerView1) as RecyclerView
                        addToList(viewSource, target, positionTarget)
                        Log.d("my", ">>> 1: $viewId")
                    }
                    // добавление в рабочее пространство
                    recyclerView2 -> {
                        target = v.rootView.findViewById<View>(recyclerView2) as RecyclerView
                        addToList(viewSource, target, positionTarget)
                        Log.d("my", ">>> 2: $viewId")
                    }
                    else -> {
                        target = v.parent as RecyclerView
                        positionTarget = v.tag as Int
                        Log.d("my", ">>> else: $viewId")
                    }
                }


            }
        }
    }

    private fun addToList(viewSource : View?, target : RecyclerView?, positionTarget : Int){
        if (viewSource != null) {
            val source = viewSource.parent as RecyclerView
            val adapterSource = source.adapter as DragHomeItemAdapter?
            val positionSource = viewSource.tag as Int
            val list: GridItemModel? = adapterSource?.getList()?.get(positionSource)
            val listSource = adapterSource?.getList()?.apply {
                removeAt(positionSource)
            }
            listSource?.let { adapterSource.updateList(it) }
            adapterSource?.notifyDataSetChanged()
            val adapterTarget = target?.adapter as DragHomeItemAdapter?
            val customListTarget = adapterTarget?.getList()
            if (positionTarget >= 0) {
                list?.let { customListTarget?.add(positionTarget, it) }
            } else {
                list?.let { customListTarget?.add(it) }
            }
            customListTarget?.let { adapterTarget.updateList(it) }
            adapterTarget?.notifyDataSetChanged()

            Log.d("my", ">>> position: $positionTarget")
        }
    }

}