package sbs.pros.parking.app_info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.AppInfoFragmentBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class AppInfoFragment: Fragment(R.layout.app_info_fragment) {

    private val binding by viewLifecycleLazy { AppInfoFragmentBinding.bind(requireView()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}