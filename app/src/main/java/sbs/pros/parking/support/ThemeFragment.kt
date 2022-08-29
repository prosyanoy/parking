package sbs.pros.parking.support

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.R
import sbs.pros.parking.databinding.ThemeFragmentBinding
import sbs.pros.parking.menu.MenuViewModel
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class ThemeFragment: Fragment(R.layout.theme_fragment) {

    private val viewModel by activityViewModels<MenuViewModel>()
    private val binding by viewLifecycleLazy { ThemeFragmentBinding.bind( requireView()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Тема")

    }
}