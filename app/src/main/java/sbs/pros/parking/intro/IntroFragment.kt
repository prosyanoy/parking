package sbs.pros.parking.intro

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentIntroBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class IntroFragment : Fragment(R.layout.fragment_intro) {

    private val binding by viewLifecycleLazy { FragmentIntroBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}