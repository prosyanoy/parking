package sbs.pros.parking.support

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentSupportBinding
import sbs.pros.parking.menu.MenuViewModel
import sbs.pros.parking.utils.navigateSafe
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class SupportFragment() : Fragment(R.layout.fragment_support) {

    private val viewModel by activityViewModels<MenuViewModel>()
    private val binding by viewLifecycleLazy { FragmentSupportBinding.bind(requireView()) }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Обратная связь")
        setupFragmentListener()

        with(binding){

            themeBtn.setSafeOnClickListener {
                findNavController().navigateSafe(R.id.action_supportFragment_to_themeFragment)
            }

            messageField.addTextChangedListener {
                it?.length?.let{ charLength -> sendBtn.isEnabled = charLength > 9 }
            }
        }
    }


    private fun setupFragmentListener(){
        setFragmentResultListener(THEME_REQUEST_KEY){ key, bundle ->
            val theme = bundle?.getString("theme")
            theme?.let { binding.themeText.text = it }
        }
    }

    companion object {
        const val THEME_REQUEST_KEY = "THEME"

    }
}