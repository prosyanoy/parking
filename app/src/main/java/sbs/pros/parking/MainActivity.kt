package sbs.pros.parking



import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.Constants.MAPKIT_API_KEY
import sbs.pros.parking.databinding.ActivityMainBinding
import sbs.pros.parking.utils.MapKitInitializer

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitInitializer.initialize(MAPKIT_API_KEY, this)
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