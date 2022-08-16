package sbs.pros.parking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import sbs.pros.parking.databinding.FragmentIntroBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class IntroFragment : Fragment(R.layout.fragment_intro) {

    private val binding by viewLifecycleLazy { FragmentIntroBinding.bind(requireView()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager2.adapter = ViewPagerAdapter()

        TabLayoutMediator(binding.intoTabLayout, binding.viewPager2)
        { tab, position ->}.attach()
    }




}