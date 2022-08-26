package sbs.pros.parking.landlord_main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentInBinding
import sbs.pros.parking.databinding.FragmentOutBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class OutFragment : Fragment(R.layout.fragment_out) {

    private val binding by viewLifecycleLazy { FragmentOutBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}