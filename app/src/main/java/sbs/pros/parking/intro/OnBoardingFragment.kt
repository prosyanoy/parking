package sbs.pros.parking.intro

import android.os.Bundle
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

class OnBoardingFragment : Fragment() {
    lateinit var dotLayout : LinearLayout
    
    var pages : Array<ItemPage> = arrayOf(
        ItemPage(R.drawable.onboarding_add_auto, "Добавьте автомобиль"),
        ItemPage(R.drawable.onboarding_choose_parking,"Выберите удобную парковку"),
        ItemPage(R.drawable.onboarding_pay,"Оплатите пареовку удобным способом"),
        ItemPage(R.drawable.onboarding_parking_history,"Смотрите историю парковок и платежей")
    )

    lateinit var dots : Array<ImageView>
    lateinit var mPager : ViewPager

    var currentPage : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_on_boarding, container, false)

        mPager = view.findViewById(R.id.pager)
        val adapter : ViewPagerAdapter = ViewPagerAdapter(view.context, pages)

        mPager.adapter = adapter
        dotLayout = view.findViewById(R.id.dotLayout)
        createDots(0, view)

        val nextPage : Button = view.findViewById(R.id.nextPage)
        nextPage.setOnClickListener(View.OnClickListener {
            if(currentPage == pages.size - 1){
                findNavController().navigate(R.id.action_onBoardingFragment_to_locationFragment)
            } else{
                mPager.currentItem = currentPage + 1
            }
        })

        mPager.currentItem = currentPage
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                currentPage = position

                createDots(position, view)
            }
        })

        val buttonScip = view.findViewById<Button>(R.id.buttonSkipOnboard)
        buttonScip.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_locationFragment)
        })

        return view
    }

    fun createDots(position: Int, view: View)
    {
        dotLayout.removeAllViews()
        dots = Array(pages.size) { ImageView(view.context) }

        for (i in pages.indices)
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