package sbs.pros.parking.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.MapFragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.MenuFragmentBinding
import sbs.pros.parking.utils.navigateSafe
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class MenuFragment: BaseMenu(R.layout.menu_fragment) {

    private val binding by viewLifecycleLazy { MenuFragmentBinding.bind( requireView()) }

    override val fragmentListener: FragmentListener
        get() = requireActivity() as FragmentListener



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("Меню")

        with(binding){

            info.setSafeOnClickListener {
                findNavController().navigateSafe(R.id.action_menuFragment_to_appInfoFragment)
            }



            support.setSafeOnClickListener {
                findNavController().navigateSafe(R.id.action_menuFragment_to_supportFragment)
            }

        }
    }




}