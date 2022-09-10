package sbs.pros.parking.landlord_intro

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_landlord_auth.*
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentLandlordAuthBinding
import sbs.pros.parking.utils.navigateSafe
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy


class LandlordAuth : Fragment(R.layout.fragment_landlord_auth){

    private var prefs: SharedPreferences? = null

    private val binding by viewLifecycleLazy { FragmentLandlordAuthBinding.bind(requireView()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = activity?.getSharedPreferences("sbs.pros.parking", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enter.setSafeOnClickListener {
            val token = token.text.toString()
            if (token == "12345") {
                prefs?.edit()?.putBoolean("firstrun", false)?.apply()
                prefs?.edit()?.putString("userType", "parkingOwner")?.apply()

                findNavController().navigateSafe(R.id.action_landlordAuth_to_landlordMenu)
            }
        }



        register.setSafeOnClickListener {
            //go to registration menu or smth
        }


    }
}