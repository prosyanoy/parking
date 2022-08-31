package sbs.pros.parking.intro

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import sbs.pros.parking.R


class LocationFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_location, container, false)

        val buttonSendRequest : Button = view.findViewById(R.id.buttonRequest)
        buttonSendRequest.setOnClickListener(View.OnClickListener {
            requestLocationPermission()
        })

        val buttonNextFragment : Button = view.findViewById(R.id.buttonSkipAuth)
        buttonNextFragment.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_locationFragment_to_SMSFragment)
        })
        return view
    }

    private fun requestLocationPermission() {
        val PERMISSIONS_REQUEST_FINE_LOCATION = 1
        requestPermissions(arrayOf("android.permission.ACCESS_FINE_LOCATION"), PERMISSIONS_REQUEST_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val PERMISSIONS_REQUEST_FINE_LOCATION = 1
        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findNavController().navigate(R.id.action_locationFragment_to_SMSFragment)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}