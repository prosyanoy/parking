package sbs.pros.parking



import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.databinding.ActivityMainBinding
import sbs.pros.parking.utils.MapKitInitializer

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private var prefs: SharedPreferences? = null

    var phone: String? = null
    var select: Boolean? = false

    val blue = Color.rgb(13, 174, 252)
    val lightBlue = Color.rgb(91, 200, 252)
    val darkBlue = Color.rgb(9, 133, 192)
    val green = Color.rgb(57, 180, 36)
    val lightGreen = Color.rgb(92, 233, 70)
    val darkGreen = Color.rgb(30, 141, 13)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val key = BuildConfig.mapkit_api_key
        MapKitInitializer.initialize(key, this)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        prefs = getSharedPreferences("sbs.pros.parking", MODE_PRIVATE)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (prefs!!.getBoolean("firstrun", true)) {
            navController.navigate(R.id.action_mapFragment2_to_introFragment)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray){

        if (requestCode == Constants.PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(Intent.makeRestartActivityTask(this.intent?.component))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}