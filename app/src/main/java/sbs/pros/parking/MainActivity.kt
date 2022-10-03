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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sbs.pros.parking.bottom_sheet.BottomSheetDialog
import sbs.pros.parking.intro.IntroActivity
import sbs.pros.parking.model.PinData
import sbs.pros.parking.utils.drawLocationPoint
import sbs.pros.parking.utils.drawSimpleBitmap
import sbs.pros.parking.utils.moveWithBottomPadding


class MainActivity : AppCompatActivity(), ClusterListener, ClusterTapListener,
    UserLocationObjectListener{

    private val MAPKIT_API_KEY = "024ae79a-58dc-4626-ac7e-1ba6ba83121e"

    private val url = "https://pros.sbs/parking/getting.php"

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
            navController.navigate(R.id.action_mapFragment_to_chooseFragment)
        } else if (prefs!!.getString("userType","autoUser") == "parkingOwner") {
            navController.navigate(R.id.action_mapFragment_to_landlordMenu)
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

    private fun setUserLocationLayer(){
        userLocationLayer = mapKit?.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = false
        userLocationLayer!!.setObjectListener(this)
    }

    private fun checkUserLocation(){
        GlobalScope.launch(Dispatchers.Main) {
            var t = 0
            while (t < 10000){
                if (userLocationLayer!!.cameraPosition()?.target != null){
                    centerCameraByUser()
                    break
                }
                delay(250L)
                t += 250
            }
        }
    }

    private fun checkFineLocationGrant(): Boolean {
        return (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun checkFineLocationDenied(): Boolean {
        return (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
                == PackageManager.PERMISSION_DENIED)
    }

    private fun checkGEOStatus(): Boolean {
        val manager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun centerCameraByUser() {
        mapView!!.map.move(
            CameraPosition(Point(userLocationLayer!!.cameraPosition()?.target!!.latitude, userLocationLayer!!.cameraPosition()?.target!!.longitude), 16f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null)
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.arrow.setIcon(ImageProvider.fromBitmap(drawLocationPoint()))
        userLocationView.arrow.setIconStyle(IconStyle().setAnchor(PointF(0.5f, 0.5f))
            .setRotationType(RotationType.ROTATE)
            .setZIndex(1f)
            .setScale(0.5f))

        val pinIcon = userLocationView.pin.useCompositeIcon()
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromBitmap(drawLocationPoint()),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {}

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {}
    //end user location

}