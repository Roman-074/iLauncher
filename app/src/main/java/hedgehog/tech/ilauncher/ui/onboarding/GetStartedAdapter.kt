package hedgehog.tech.ilauncher.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.ilauncher.R

class GetStartedAdapter: RecyclerView.Adapter<GetStartedAdapter.OnBoardingViewHolder>() {

    private val list: List<Int> = arrayListOf(
        R.drawable.bg_workspace_0,
        R.drawable.bg_workspace_1,
        R.drawable.bg_workspace_2
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.view_vp_getstarted_item, parent, false)
        return  OnBoardingViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.setData(list[position])
    }

    override fun getItemCount(): Int  = list.size



    class OnBoardingViewHolder(
        itemView: View,
        var bg: ImageView = itemView.findViewById(R.id.getstarted_item_image)
    ) : RecyclerView.ViewHolder(itemView){

            fun setData(image: Int){
                bg.setImageResource(image)
            }
    }

}