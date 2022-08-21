package sbs.pros.parking.intro

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import sbs.pros.parking.databinding.CustomTabBinding
import sbs.pros.parking.databinding.FragmentAuthBinding
import sbs.pros.parking.utils.viewLifecycleLazy


class AuthFragment : Fragment(sbs.pros.parking.R.layout.fragment_auth) {

    private val binding by viewLifecycleLazy { FragmentAuthBinding.bind( requireView()) }

    var def: ColorStateList? = null
    var item1: TextView? = null
    var item2: TextView? = null
    var item3: TextView? = null
    var select: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabBinding = binding.tabView
        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        item1 = tabBinding.item1
        item2 = tabBinding.item2
        item3 = tabBinding.item3
        item1!!.setOnClickListener {
            select!!.animate().x(0f).duration = 100
            item1!!.setTextColor(Color.WHITE)
            item2!!.setTextColor(def)
            item3!!.setTextColor(def)
        }
        item2!!.setOnClickListener {
            item1!!.setTextColor(def)
            item2!!.setTextColor(Color.WHITE)
            item3!!.setTextColor(def)
            val size = item2!!.width
            select!!.animate().x(size.toFloat()).duration = 100
        }
        item3!!.setOnClickListener {
            item1!!.setTextColor(def)
            item3!!.setTextColor(Color.WHITE)
            item2!!.setTextColor(def)
            val size = item2!!.width * 2
            select!!.animate().x(size.toFloat()).duration = 100
        }
        select = tabBinding.select
        def = item2!!.textColors
        /*val buttonNextFragment = view.findViewById<Button>(R.id.button)
        buttonNextFragment.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_carRegistrationFragment)
        })*/
    }
}