package sbs.pros.parking.users_intro

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import sbs.pros.parking.databinding.FragmentLocationBinding
import sbs.pros.parking.utils.viewLifecycleLazy
import kotlin.math.roundToInt


class LocationFragment : Fragment(sbs.pros.parking.R.layout.fragment_location) {

    private val binding by viewLifecycleLazy { FragmentLocationBinding.bind( requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSendRequest : Button = view.findViewById(sbs.pros.parking.R.id.buttonRequest)
        buttonSendRequest.setOnClickListener(View.OnClickListener {
            requestLocationPermission()
        })

        binding.guideline.setGuidelineBegin(((requireContext().resources.displayMetrics.widthPixels/2-135)/requireContext().resources.displayMetrics.density).roundToInt())

        val iconImage = binding.iconImage
        val radius = 150 // corner radius, higher value = more rounded
        Glide.with(this)
            .load("file:///android_asset/PPicon.png")
            .override(300, 300)
            .centerCrop() // scale image to fill the entire ImageView
            .transform(RoundedCorners(radius))
            .into(iconImage)

    }

    private fun requestLocationPermission() {
        val PERMISSIONS_REQUEST_FINE_LOCATION = 1
        requestPermissions(arrayOf("android.permission.ACCESS_FINE_LOCATION"), PERMISSIONS_REQUEST_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val PERMISSIONS_REQUEST_FINE_LOCATION = 1
        /*if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {*/
                findNavController().navigate(sbs.pros.parking.R.id.action_locationFragment_to_authFragment)
            //}
        //}
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}