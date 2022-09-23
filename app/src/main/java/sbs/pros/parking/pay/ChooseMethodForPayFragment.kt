package sbs.pros.parking.pay

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentChooseMethodForPayBinding
import sbs.pros.parking.databinding.FragmentPayBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class ChooseMethodForPayFragment : Fragment(R.layout.fragment_choose_method_for_pay) {

    private val binding by viewLifecycleLazy { FragmentChooseMethodForPayBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
