package sbs.pros.parking.intro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import sbs.pros.parking.MainActivity
import sbs.pros.parking.R
import sbs.pros.parking.databinding.ActivityIntroBinding


class IntroActivity : AppCompatActivity() {

    private var prefs: SharedPreferences? = null
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
    }


    override fun onBackPressed() {}
}