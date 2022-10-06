package sbs.pros.parking.pay

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentPayBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class PayFragment : Fragment(R.layout.fragment_pay) {

    private val binding by viewLifecycleLazy { FragmentPayBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.payFragmentChooseLayout.setOnClickListener {
            findNavController().navigate(R.id.action_payFragment_to_chooseMethodForPayFragment)
        }
    }
}
