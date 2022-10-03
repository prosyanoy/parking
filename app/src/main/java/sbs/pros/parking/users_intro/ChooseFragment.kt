package sbs.pros.parking.users_intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentChooseBinding
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

class ChooseFragment : Fragment(R.layout.fragment_choose) {

    private val binding by viewLifecycleLazy { FragmentChooseBinding.bind( requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.autoUser.setSafeOnClickListener {
            findNavController().navigate(R.id.action_chooseFragment_to_onBoardingFragment)
        }

        binding.parkinOwner.setSafeOnClickListener {
            findNavController().navigate(R.id.action_chooseFragment_to_landlordAuth)
        }
    }
}