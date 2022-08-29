package sbs.pros.parking.support

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.R
import sbs.pros.parking.databinding.ThemeFragmentBinding
import sbs.pros.parking.menu.BaseMenu
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class ThemeFragment: BaseMenu(R.layout.theme_fragment) {

    private val binding by viewLifecycleLazy { ThemeFragmentBinding.bind( requireView()) }
    override val fragmentListener: FragmentListener
        get() = requireActivity() as FragmentListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("Тема")

    }
}