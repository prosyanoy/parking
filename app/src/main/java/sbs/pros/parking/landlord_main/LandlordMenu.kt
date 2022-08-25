package sbs.pros.parking.landlord_main

import com.google.android.material.bottomnavigation.BottomNavigationView
import sbs.pros.parking.landlord_main.InFragment
import sbs.pros.parking.landlord_main.ParkedFragment
import sbs.pros.parking.landlord_main.OutFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentLandlordMenuBinding
import sbs.pros.parking.databinding.FragmentMapBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class LandlordMenu : Fragment(R.layout.fragment_landlord_menu) {
    private var bottomNavigationView: BottomNavigationView? = null
    private val inFragment = InFragment()
    private val parkedFragment = ParkedFragment()
    private val outFragment = OutFragment()


    private val binding by viewLifecycleLazy { FragmentLandlordMenuBinding.bind( requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}