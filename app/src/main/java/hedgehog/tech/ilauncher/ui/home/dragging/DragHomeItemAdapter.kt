package hedgehog.tech.ilauncher.ui.home.dragging

import android.content.ClipData
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.ilauncher.R
import hedgehog.tech.ilauncher.data.model.GridItemModel

class DragHomeItemAdapter(
    private var list: List<GridItemModel>
) : RecyclerView.Adapter<DragHomeItemAdapter.CustomViewHolder?>(), View.OnTouchListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CustomViewHolder(inflater, parent)
    }

    //todo ограничить размер recycler'a
    override fun getItemCount(): Int = list.size

    fun updateList(list: MutableList<GridItemModel>) {
        this.list = list
    }

    fun getList(): MutableList<GridItemModel> = this.list.toMutableList()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.d("my", "onTouch: ${event?.action}")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v?.startDragAndDrop(data, shadowBuilder, v, 0)
                return true
            }
        }
        return false
    }

    val dragHomeItemInstance: DragHomeItemListener
        get() = DragHomeItemListener()

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        holder.text?.text = list[position].appName
        holder.frameLayout?.tag = position
        holder.image?.setImageDrawable(list[position].icon)
        holder.frameLayout?.setOnTouchListener(this)
    }

    class CustomViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.view_home_grid_item, parent, false)) {
//        var text: TextView? = null
        var frameLayout: LinearLayout? = null
        var image : ImageView? = null

        init {
//            text = itemView.findViewById(R.id.home_grid_item_name)
            frameLayout = itemView.findViewById(R.id.home_grid_item_layout)
            image = itemView.findViewById(R.id.home_grid_item_logo)
        }
    }

}