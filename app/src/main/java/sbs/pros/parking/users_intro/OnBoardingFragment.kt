package sbs.pros.parking.users_intro

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentOnBoardingBinding
import sbs.pros.parking.utils.viewLifecycleLazy


class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    var pages: Array<ItemPage> = arrayOf(
        ItemPage(R.layout.item_page_on_boarding_search_parking),
        ItemPage(R.layout.item_page_on_boarding_pay_for_parking),
        ItemPage(R.layout.item_page_on_boarding_tariff_for_parking),
        ItemPage(R.layout.item_page_on_boarding_geolocation)
    )

    lateinit var mPager: ViewPager

    private var currentPage: Int = 0
    private val startProgressInProgressBar = 33

    private val binding by viewLifecycleLazy { FragmentOnBoardingBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentPage == pages.size - 1) {
            hideView()
        }

        binding.onBoardingProgressBar.progress = startProgressInProgressBar

        mPager = binding.onBoardingViewPager
        val adapter = ViewPagerAdapter(view.context, pages)

        mPager.adapter = adapter

        binding.onBoardingNextPageFAB.setOnClickListener {
            if (currentPage == pages.size - 1) {
                if (checkFineLocationGrant()) {
                    findNavController().navigate(R.id.action_onBoardingFragment_to_authFragment)
                } else {
                    findNavController().navigate(R.id.action_onBoardingFragment_to_locationFragment)
                }
            } else {
                mPager.currentItem = currentPage + 1
            }
        }

        mPager.currentItem = currentPage
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPage = position

                when (currentPage) {
                    pages.size - 1 -> {
                        hideView()
                    }

                    else -> {
                        showView()
                        binding.onBoardingProgressBar.progress = (
                                ((currentPage + 1).toDouble() / (pages.size - 1)) * 100).toInt()
                    }
                }
            }
        })

        binding.onBoardingButtonBack.setOnClickListener {
            if (mPager.currentItem != 0) {
                mPager.currentItem = currentPage - 1
            } else {
                findNavController().navigate(R.id.action_onBoardingFragment_to_chooseFragment)
            }
        }

        binding.onBoardingButtonSkip.setOnClickListener {
            if (checkFineLocationGrant()) {
                findNavController().navigate(R.id.action_onBoardingFragment_to_authFragment)
            } else {
                findNavController().navigate(R.id.action_onBoardingFragment_to_locationFragment)
            }
        }
    }

    private fun hideView() {
        with(binding) {
            onBoardingButtonBack.visibility = View.GONE
            onBoardingButtonSkip.visibility = View.GONE
            onBoardingProgressBar.visibility = View.GONE
        }
    }

    private fun showView() {
        with(binding) {
            onBoardingButtonBack.visibility = View.VISIBLE
            onBoardingButtonSkip.visibility = View.VISIBLE
            onBoardingProgressBar.visibility = View.VISIBLE
        }
    }

    private fun checkFineLocationGrant(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            "android.permission.ACCESS_FINE_LOCATION"
        )
                == PackageManager.PERMISSION_GRANTED)
    }
}