package sbs.pros.parking.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sbs.pros.parking.R
import sbs.pros.parking.databinding.MenuFragmentBinding
import sbs.pros.parking.utils.navigateSafe
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

class MenuFragment: Fragment(R.layout.menu_fragment) {

    private val binding by viewLifecycleLazy { MenuFragmentBinding.bind( requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding){

            info.setSafeOnClickListener {
                findNavController().navigateSafe(R.id.action_menuFragment_to_appInfoFragment)
            }

        }
    }

}