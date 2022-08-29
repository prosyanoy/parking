package sbs.pros.parking.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.MapFragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.MenuFragmentBinding
import sbs.pros.parking.utils.navigateSafe
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class MenuFragment() : Fragment(R.layout.menu_fragment) {

    private val viewModel by activityViewModels<MenuViewModel>()
    private val binding by viewLifecycleLazy { MenuFragmentBinding.bind( requireView()) }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Меню")

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