package sbs.pros.parking.intro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import sbs.pros.parking.MainActivity
import sbs.pros.parking.R
import sbs.pros.parking.ViewPagerAdapter
import sbs.pros.parking.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding : ActivityIntroBinding

    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager2.adapter = ViewPagerAdapter()

        TabLayoutMediator(binding.intoTabLayout, binding.viewPager2)
        { tab, position ->}.attach()

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener(View.OnClickListener {
            prefs!!.edit().putBoolean("firstrun", false).commit()

            startActivity(Intent(this, MainActivity::class.java))
        })
    }

    override fun onBackPressed() {}
}