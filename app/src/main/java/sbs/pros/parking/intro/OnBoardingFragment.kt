package sbs.pros.parking.intro

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import sbs.pros.parking.R
import java.util.*

class OnBoardingFragment : Fragment() {
    lateinit var dotLayout : LinearLayout
    var images : Array<Int> = arrayOf(R.drawable.parking, R.drawable.old)
    var texts : Array<String> = arrayOf("первый","второй")
    lateinit var dots : Array<ImageView>
    private lateinit var mPager : ViewPager

    var currentPager : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_on_boarding, container, false)

        mPager = view.findViewById(R.id.pager)
        val adapter : ViewPagerAdapter = ViewPagerAdapter(view.context, images, texts)

        mPager.adapter = adapter
        dotLayout = view.findViewById(R.id.dotLayout)
        createDots(0, view)
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                currentPager = position
                createDots(position, view)
            }
        })

        val scip = view.findViewById<Button>(R.id.skipOnboard)
        scip.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_authFragment)
        })


        val nextPage = view.findViewById<Button>(R.id.skipOnboard)
        nextPage.setOnClickListener(View.OnClickListener {
            updatePager()
        })

        return view
    }

    fun updatePager()
    {
        var handler = Handler()
        val Update : Runnable = Runnable {
            if (currentPager == images.size)
            {
                currentPager = 0
            }
            mPager.setCurrentItem(currentPager++, true)
        }

        run() {
            handler.post(Update)
        }
    }

    private fun getItem(i: Int): Int {
        return mPager.currentItem + i
    }

    fun createDots(position: Int, view: View)
    {
        if (dotLayout!=null)
        {
            dotLayout.removeAllViews()
        }
        dots = Array(images.size,{i -> ImageView(view.context) })

        for (i in 0..images.size - 1)
        {
            dots[i] = ImageView(view.context)
            if (i == position)
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.active_dots))
            }
            else
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.inactive_dots))
            }

            var params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                ViewPager.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            params.setMargins(4,0,4,0)
            dotLayout.addView(dots[i], params)
        }
    }
}