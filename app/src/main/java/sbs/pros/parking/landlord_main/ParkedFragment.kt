package sbs.pros.parking.landlord_main

import sbs.pros.parking.landlord_main.Parker
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentInBinding
import sbs.pros.parking.databinding.FragmentParkedBinding
import sbs.pros.parking.utils.viewLifecycleLazy
import java.util.ArrayList

class ParkedFragment : Fragment(R.layout.fragment_parked) {

    private val binding by viewLifecycleLazy { FragmentParkedBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}