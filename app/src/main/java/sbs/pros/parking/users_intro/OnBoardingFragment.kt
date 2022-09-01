package sbs.pros.parking.users_intro

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentOnBoardingBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {
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

    private val binding by viewLifecycleLazy { FragmentOnBoardingBinding.bind( requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPager = binding.pager
        val adapter : ViewPagerAdapter = ViewPagerAdapter(view.context, pages)

        mPager.adapter = adapter
        dotLayout = view.findViewById(R.id.dotLayout)
        createDots(0, view)

        val nextPage = binding.nextPage
        nextPage.setOnClickListener(View.OnClickListener {
            if(currentPage == pages.size - 1){
                if(checkFineLocationGrant()){
                    findNavController().navigate(R.id.action_onBoardingFragment_to_authFragment)
                }else{
                    findNavController().navigate(R.id.action_onBoardingFragment_to_locationFragment)
                }
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

                if (position == pages.size - 1) {
                    binding.nextPage.text = "Продолжить"
                }

                createDots(position, view)
            }
        })

        val buttonScip = binding.buttonSkipOnboard

        buttonScip.setOnClickListener(View.OnClickListener {
          if(checkFineLocationGrant()){
              findNavController().navigate(R.id.action_onBoardingFragment_to_authFragment)
          }else{
              findNavController().navigate(R.id.action_onBoardingFragment_to_locationFragment)
          }
        })
    }
    /*fun introClose () {
        if(checkFineLocationGrant()){
            findNavController().navigate(R.id.action_onBoardingFragment_to_authFragment)
        }else{
            findNavController().navigate(R.id.action_onBoardingFragment_to_locationFragment)
        }
    }*/

    fun createDots(position: Int, view: View)
    {
        dotLayout.removeAllViews()
        dots = Array(pages.size) { ImageView(view.context) }

        val params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewPager.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        params.setMargins(4,0,4,0)

        for (i in 0 until position) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.inactive_dots))
            dotLayout.addView(dots[i], params)
        }

        dots[position].setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.active_dots))
        dotLayout.addView(dots[position], params)

        for (i in position+1 until pages.size) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.inactive_dots))
            dotLayout.addView(dots[i], params)
        }
    }

    private fun checkFineLocationGrant(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), "android.permission.ACCESS_FINE_LOCATION")
                == PackageManager.PERMISSION_GRANTED)
    }
}