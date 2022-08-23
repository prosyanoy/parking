package sbs.pros.parking.intro

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import sbs.pros.parking.MainActivity
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentConfirmationBinding
import sbs.pros.parking.utils.viewLifecycleLazy


class ConfirmationFragment : Fragment(R.layout.fragment_confirmation) {

    private val binding by viewLifecycleLazy { FragmentConfirmationBinding.bind( requireView()) }

    private var code: String? = null

    private val TAG = "ConfirmationFragment"

    var phone: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phone = (activity as MainActivity).phone
        binding.phoneNumber.text = phone

        confirmationRequest()

        binding.codeInputView.onCompleteEventDelay = 100

        binding.codeInputView.addOnCompleteListener {
            if (it != code.toString()) {
                Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_SHORT).show()
                val confirmationSecond = binding.confirmationSecond
                val c = ConstraintSet()
                c.clone(context, R.layout.fragment_confirmation)
                c.connect(confirmationSecond.id,ConstraintSet.TOP,binding.phoneNumber.id,ConstraintSet.BOTTOM,20)
                c.applyTo(view.findViewById(R.id.confirmationLayout))
            } else {
                TODO()
                findNavController().navigate(R.id.action_confirmationFragment_to_carRegistrationFragment)
            }
        }
    }

        //GetTime(requireContext())

    val timer = object: CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000
            binding.repeatedRequest.text =
                getString(R.string.remaining1) + " $seconds " + getString(R.string.remaining2)
        }

        override fun onFinish() {
            binding.repeatedRequest.text = getString(R.string.repeated_request)
            binding.repeatedRequest.setTextColor((activity as MainActivity).blue)

            binding.repeatedRequest.setOnClickListener(View.OnClickListener {
                confirmationRequest()
                binding.repeatedRequest.isClickable = false
                binding.repeatedRequest.setTextColor(Color.parseColor("#6D72797E"))
            })
        }
    }
        fun haveCar(phone: String) {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "https://pros.sbs/getting.php?apicall=car&phone=$phone"
            val stringRequest = object : StringRequest(
                Method.GET,
                url,
                {},
                {}) {}
            queue.add(stringRequest)
        }

    fun confirmationRequest () {
        if ((activity as MainActivity).select == true) {
            binding.confirmationSecond.text = getString(R.string.flash_call_second)
            var code = (1000..9999).random()
            SMSRequest(requireContext(), code, phone!!)
            timer.start()
        } else {
            binding.confirmationSecond.text = getString(R.string.sms_second)
            var code = (1000..9999).random()
            FlashCallRequest(requireContext(), code, phone!!)
            timer.start()
        }
    }

    companion object {

    }
}

class SMSRequest (context : Context, code : Int, phone : String) {
    val queue = Volley.newRequestQueue(context)
    val url = "https://my3.webcom.mobi/sendsms.php?user=prosyanoy&pwd=jhknlkjn&sadr=ProParking&text=Код: $code&dadr=$phone"
    val stringRequest = StringRequest(
        url,
        { smsId ->
            CheckStatus(context, smsId)
        },
        {
            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT)
        })
    init {
        queue.add(stringRequest)
    }
}

class FlashCallRequest (context : Context, code : Int, phone : String) {
    val queue = Volley.newRequestQueue(context)
    val url = "https://my3.webcom.mobi/sendsms.php?user=prosyanoy&pwd=jhknlkjn&sadr=ProParking&text=Код: $code&dadr=$phone"
    val stringRequest = StringRequest(
        url,
        { smsId ->
            CheckStatus(context, smsId)
        },
        {
            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT)
        })
    init {
        queue.add(stringRequest)
    }
}

class CheckStatus (context : Context, smsId : String) {
    val queue = Volley.newRequestQueue(context)
    val url = "https://my3.webcom.mobi/sendsms.php?user=prosyanoy&pwd=jhknlkjn&smsid=$smsId"
    val stringRequest = StringRequest(
        url,
        { response ->
            when (response) {
                "not_deliver" -> Toast.makeText(context, R.string.not_deliver, Toast.LENGTH_SHORT).show()
                "expired" -> Toast.makeText(context, R.string.expired, Toast.LENGTH_SHORT).show()
                "deliver" -> Toast.makeText(context, R.string.deliver, Toast.LENGTH_SHORT).show()
            }
        },
        {
            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show()
        })
    init {
        queue.add(stringRequest)
    }
}

class GetTime(context: Context) {
    val queue = Volley.newRequestQueue(context)
    val url = ""
    val prefs = context.getSharedPreferences("time", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val stringRequest = StringRequest(
        url,
        {
            val json = JSONObject(it)
            val datetime = json.getString("datetime")
            val day = datetime.substring(9, 11)
            Log.i("GetTime", day)
            //editor.putString("time", response)
        },
        {

        })
    init {
        queue.add(stringRequest)
    }
}