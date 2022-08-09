package sbs.pros.parking.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import sbs.pros.parking.R


class ViewPagerAdapter : PagerAdapter {


    var itemPages : Array<ItemPage>
    var con : Context

    constructor(con: Context, itemPages : Array<ItemPage>) : super() {
        this.con = con
        this.itemPages = itemPages
    }

    override fun getCount(): Int {return itemPages.size}

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view : View = inflater.inflate(R.layout.item_page, container, false)
        val img : ImageView = view.findViewById(R.id.img) as ImageView
        val txt : TextView = view.findViewById(R.id.textView2) as TextView

        img.setImageResource(itemPages[position].image)
        txt.setText(itemPages[position].text)
        container.addView(view)


        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}