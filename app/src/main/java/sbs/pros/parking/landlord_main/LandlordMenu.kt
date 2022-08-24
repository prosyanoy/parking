package sbs.pros.parking.landlord_main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.landlord_main.LandlordMenu

class LandlordMenu : Fragment() {

    private val fragmentIn = FragmentIn()
    private val fragmentParked = FragmentParked()
    private val fragmentOut = FragmentOut()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landlord_menu, container, false)
    }

    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}