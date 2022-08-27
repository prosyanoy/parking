package sbs.pros.parking.support

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentSupportBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class SupportFragment: Fragment(R.layout.fragment_support) {

    private val binding by viewLifecycleLazy { FragmentSupportBinding.bind(requireView()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){

            messageField.addTextChangedListener {
                it?.length?.let{ charLength -> sendBtn.isEnabled = charLength > 9 }
            }
        }
    }
}