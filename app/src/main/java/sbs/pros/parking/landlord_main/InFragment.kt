package sbs.pros.parking.landlord_main

import sbs.pros.parking.landlord_main.Parker
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentInBinding
import sbs.pros.parking.utils.viewLifecycleLazy
import java.util.ArrayList

class InFragment : Fragment(R.layout.fragment_in) {

    var inParkers: MutableList<Parker> = ArrayList()

    private val binding by viewLifecycleLazy { FragmentInBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inParkers.add(Parker("A 123 BC 45", "12:30", 3))
    }
}