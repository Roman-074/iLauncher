package hedgehog.tech.ilauncher.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.data.model.GridItemModel


// todo был заменен на адаптер для перетаскивания вьюх
class HomeVPAppGridAdapter(
        context: Context?,
        resource: Int, // бесполезная переменная, но других подходящих конструкторов нет
        objects: MutableList<GridItemModel>,
): ArrayAdapter<GridItemModel>(context!!, resource, objects) {

    private lateinit var appLayout: LinearLayout

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = LayoutInflater.from(context).inflate(R.layout.view_home_grid_item, parent, false)
        val gridItemModel: GridItemModel? = getItem(position)
        gridItemModel?.let {
            appLayout = listItemView.findViewById<LinearLayout?>(R.id.home_grid_item_layout)
//            appLayout.setOnDragListener(dragListener)

            val imageView = listItemView?.findViewById<ImageView?>(R.id.home_grid_item_logo)
            imageView?.setImageDrawable(it.icon)
//            setCorners(imageView)
            listItemView.findViewById<TextView?>(R.id.home_grid_item_name)?.text = it.appName
        }

        return listItemView!!
    }

    // TODO нужно будет доработать вьюху
//    private fun setCorners(view: View?){
//        val backgroundDrawable = MaterialShapeDrawable()
//        backgroundDrawable.setCornerSize(25f)
//        view?.background = backgroundDrawable
//    }

    private var trig = false
    private val dragListener = View.OnDragListener { view, event ->
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                trig = false
                Log.d("my", ":::::: Drag _ Started")
                true
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d("my", ":::::: Drag _ Entered")
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("my", ":::::: Drag _ Exited")
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                Log.d("my", ":::::: Drag _ Location")
                true
            }

            DragEvent.ACTION_DROP -> {
                Log.d("my", ":::::: Drag _ Drop")
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d("my", ":::::: Drag _ Ended")
                view.visibility = View.VISIBLE
                true
            }

            else -> false
        }
    }

}