package sbs.pros.parking.landlord_main

import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_landlord_menu.*
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentLandlordMenuBinding
import sbs.pros.parking.databinding.FragmentMapBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class LandlordMenu : Fragment(R.layout.fragment_landlord_menu) {

    private val inFragment = InFragment()
    private val parkedFragment = ParkedFragment()
    private val outFragment = OutFragment()


    private val binding by viewLifecycleLazy { FragmentLandlordMenuBinding.bind( requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        replaceFragment(inFragment)

        bottom_menu.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.`in` -> replaceFragment(inFragment)
                R.id.out -> replaceFragment(outFragment)
                R.id.parked -> replaceFragment(parkedFragment)
            }
            true
        }
    }


    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){


            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}