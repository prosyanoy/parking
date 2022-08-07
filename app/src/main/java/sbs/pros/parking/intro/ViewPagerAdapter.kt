package sbs.pros.parking.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import sbs.pros.parking.R

class ViewPagerAdapter : PagerAdapter {

    var con : Context
    var images : Array<Int>
    var texts : Array<String>

    constructor(con: Context, path: Array<Int>, texts: Array<String>) : super() {
        this.con = con
        this.images = path
        this.texts = texts
    }

    override fun getCount(): Int {return images.size}

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var inflater: LayoutInflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var view : View = inflater.inflate(R.layout.item_page, container, false)
        var img : ImageView = view.findViewById(R.id.img) as ImageView
        var txt : TextView = view.findViewById(R.id.textView2) as TextView

        img.setImageResource(images[position])
        txt.setText(texts[position])
        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }
}