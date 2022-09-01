package sbs.pros.parking.users_intro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import sbs.pros.parking.MainActivity
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentCarRegistrationBinding
import sbs.pros.parking.utils.viewLifecycleLazy


class CarRegistrationFragment : Fragment(R.layout.fragment_car_registration) {

    private val binding by viewLifecycleLazy { FragmentCarRegistrationBinding.bind( requireView()) }

    private val TAG = "CarRegistrationFragment"

    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = activity?.getSharedPreferences("sbs.pros.parking", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val letters = PredefinedSlots.SINGLE_SLOT
        val slots = UnderscoreDigitSlotsParser().parseSlots("___")
        val sum = letters + slots

        val formatWatcher: FormatWatcher = MaskFormatWatcher(
            MaskImpl.createNonTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        )
        formatWatcher.installOn(binding.editTextCarNumber)



        val openMap = binding.buttonSkipReg
        openMap.setOnClickListener {
            prefs?.edit()?.putBoolean("firstrun", false)?.apply()
            prefs?.edit()?.putString("userType", "autoUser")?.apply()

            startActivity(Intent(context, MainActivity::class.java))
        }
    }
}