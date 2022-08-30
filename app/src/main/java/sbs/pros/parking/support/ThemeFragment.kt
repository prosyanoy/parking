package sbs.pros.parking.support

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.R
import sbs.pros.parking.databinding.ThemeFragmentBinding
import sbs.pros.parking.menu.MenuViewModel
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class ThemeFragment: Fragment(R.layout.theme_fragment) {

    private val viewModel by activityViewModels<MenuViewModel>()
    private val binding by viewLifecycleLazy { ThemeFragmentBinding.bind( requireView()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Тема")

        with(binding){
            mode.setSafeOnClickListener {
                setFragmentResult(SupportFragment.THEME_REQUEST_KEY, bundleOf(mode.text.toString() to "theme"))
                findNavController().navigateUp()
            }

            paymentCheck.setSafeOnClickListener {
                setFragmentResult(SupportFragment.THEME_REQUEST_KEY, bundleOf(paymentCheck.text.toString() to "theme"))
                findNavController().navigateUp()
            }

            refund.setSafeOnClickListener {
                setFragmentResult(SupportFragment.THEME_REQUEST_KEY, bundleOf(refund.text.toString() to "theme"))
                findNavController().navigateUp()
            }

            others.setSafeOnClickListener {
                setFragmentResult(SupportFragment.THEME_REQUEST_KEY, bundleOf(others.text.toString() to "theme"))
                findNavController().navigateUp()
            }

            rating.setSafeOnClickListener {
                setFragmentResult(SupportFragment.THEME_REQUEST_KEY, bundleOf(rating.text.toString() to "theme"))
                findNavController().navigateUp()
            }


        }
    }
}