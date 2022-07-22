package sbs.pros.parking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import sbs.pros.parking.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding : ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager2.adapter = ViewPagerAdapter()

        TabLayoutMediator(binding.intoTabLayout, binding.viewPager2)
        { tab, position ->}.attach()
    }

    override fun onBackPressed() {}
}