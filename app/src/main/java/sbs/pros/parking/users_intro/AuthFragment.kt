package sbs.pros.parking.users_intro

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import sbs.pros.parking.MainActivity
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentAuthBinding
import sbs.pros.parking.utils.viewLifecycleLazy


class AuthFragment : Fragment(sbs.pros.parking.R.layout.fragment_auth) {

    private val binding by viewLifecycleLazy { FragmentAuthBinding.bind( requireView()) }

    var def: ColorStateList? = null
    var flashCall: TextView? = null
    var sms: TextView? = null
    var select: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabBinding = binding.tabView
        flashCall = tabBinding.flashCall
        sms = tabBinding.sms
        val confirmationInstruction = binding.confirmationInstruction

        flashCall!!.setOnClickListener {
            select!!.animate().x(0f).duration = 100
            flashCall!!.setTextColor(Color.WHITE)
            sms!!.setTextColor(def)

            (activity as MainActivity).select = true

            confirmationInstruction.text = getString(R.string.flash_call_instruction)
        }
        sms!!.setOnClickListener {
            flashCall!!.setTextColor(def)
            sms!!.setTextColor(Color.WHITE)
            val size = sms!!.width
            select!!.animate().x(size.toFloat()).duration = 100

            (activity as MainActivity).select = false

            confirmationInstruction.text = getString(R.string.sms_instruction)
        }
        select = tabBinding.select
        def = sms!!.textColors

        val slots = PredefinedSlots.RUS_PHONE_NUMBER
            //UnderscoreDigitSlotsParser().parseSlots("+7 (___) ___-____")
        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = false
        val formatWatcher = MaskFormatWatcher(mask)
        formatWatcher.installOn(binding.numberEdit)

        val nextButton = binding.nextButton
        nextButton.setOnClickListener(View.OnClickListener {
            if (binding.numberEdit.text.toString().length != 18) {
                Toast.makeText(requireContext(), getString(R.string.invalid_number), Toast.LENGTH_SHORT).show()
            } else {
                (activity as MainActivity).phone = binding.numberEdit.text.toString() as? String
                findNavController().navigate(R.id.action_authFragment_to_confirmationFragment)
            }
        })
    }
}