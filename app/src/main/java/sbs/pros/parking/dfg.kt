package sbs.pros.parking

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import sbs.pros.parking.dfg
import com.yandex.mapkit.location.LocationStatus

class dfg : AppCompatActivity() {
    private var myLocation: Point? = null
    fun onLocationUpdated(location: Location) {
        if (myLocation == null) {
            //moveCamera(location.getPosition(), 11f);
        }
        myLocation = location.position
        //Log.w(TAG, "my location - " + myLocation!!.latitude + "," + myLocation!!.longitude)
    }

    fun onLocationStatusUpdated(locationStatus: LocationStatus) {
        if (locationStatus == LocationStatus.NOT_AVAILABLE) {
            //Snackbar.make(rootCoordinatorLayout, "lkj", Snackbar.LENGTH_LONG).show();
        }
    }
}