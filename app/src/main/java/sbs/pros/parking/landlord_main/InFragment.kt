package sbs.pros.parking.landlord_main

import sbs.pros.parking.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import sbs.pros.parking.databinding.FragmentInBinding
import sbs.pros.parking.utils.viewLifecycleLazy


class InFragment : Fragment(R.layout.fragment_in) {

    //var inParkers: MutableList<Parker> = ArrayList()

    private val binding by viewLifecycleLazy { FragmentInBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inflater = layoutInflater
        val parent = inflater.inflate(R.layout.tile_in, null) as RelativeLayout

        for (i in 0..5) {
            val custom: View = inflater.inflate(R.layout.tile_in, null)
            val tv = custom.findViewById<View>(R.id.car_number1) as TextView
            tv.text = "X $i$i$i XX $i$i"
            parent.addView(custom)
        }




        //inParkers.add(Parker("A 123 BC 45", "12:30", 3))
    }
}