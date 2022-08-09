package sbs.pros.parking

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import sbs.pros.parking.intro.IntroActivity

class SplashActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

        if (prefs!!.getBoolean("firstrun", true)) {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


    }

}