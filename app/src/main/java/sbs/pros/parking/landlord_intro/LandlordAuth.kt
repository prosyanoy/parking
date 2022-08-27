package sbs.pros.parking.landlord_intro

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentLandlordAuthBinding
import sbs.pros.parking.databinding.FragmentOutBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class LandlordAuth : Fragment(R.layout.fragment_landlord_auth){

    private val binding by viewLifecycleLazy { FragmentLandlordAuthBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}