package sbs.pros.parking.support

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentSupportBinding
import sbs.pros.parking.menu.BaseMenu
import sbs.pros.parking.utils.navigateSafe
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

@AndroidEntryPoint
class SupportFragment: BaseMenu(R.layout.fragment_support) {

    private val binding by viewLifecycleLazy { FragmentSupportBinding.bind(requireView()) }
    override val fragmentListener: FragmentListener
        get() = requireActivity() as FragmentListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("Обратная связь")

        with(binding){

            themeBtn.setSafeOnClickListener {
                findNavController().navigateSafe(R.id.action_supportFragment_to_themeFragment)
            }

            messageField.addTextChangedListener {
                it?.length?.let{ charLength -> sendBtn.isEnabled = charLength > 9 }
            }
        }
    }
}