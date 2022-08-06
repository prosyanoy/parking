package sbs.pros.parking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter : RecyclerView.Adapter<PagerVH>() {

    private val pictures = intArrayOf(
        R.drawable.parking,
        R.drawable.old,

    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))

    override fun getItemCount(): Int = pictures.size

    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val container = findViewById<RelativeLayout>(R.id.container)
        tvTitle.text = "item $position"
        val imageView = ImageView(context)
        imageView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        imageView.setImageResource(pictures[position])
        container.addView(imageView)
        //container.setBackgroundResource(colors[position])
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)