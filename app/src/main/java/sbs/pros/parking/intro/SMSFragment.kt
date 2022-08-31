package sbs.pros.parking

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import sbs.pros.parking.databinding.FragmentSmsBinding
import sbs.pros.parking.utils.viewLifecycleLazy

class SMSFragment : Fragment(R.layout.fragment_sms) {

    private var prefs: SharedPreferences? = null

    private val binding by viewLifecycleLazy { FragmentSmsBinding.bind(requireView()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = activity?.getSharedPreferences("sbs.pros.parking", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toMap.setOnClickListener{
            prefs?.edit()?.putBoolean("firstrun", false)?.apply()
            prefs?.edit()?.putString("userType", "autoUser")?.apply()

            findNavController().navigate(R.id.action_SMSFragment_to_mapFragment)
        }


        val code = (1000..9999).random()
        val phone = "79673204350"

        //SMSRequest(requireContext(), code, phone)

    }




}

class SMSRequest (context : Context, code : Int, phone : String) {
    val queue = Volley.newRequestQueue(context)
    val url = "https://my3.webcom.mobi/sendsms.php?user=prosyanoy&pwd=jhknlkjn&sadr=ProParking&text=Код: $code&dadr=$phone"
    val stringRequest = StringRequest(
        url,
        { response ->
            CheckStatus(context, response)
        },
        {
            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT)
        })
    init {
        queue.add(stringRequest)
    }
}

class CheckStatus (context : Context, smsid : String) {
    val queue = Volley.newRequestQueue(context)
    val url = "https://my3.webcom.mobi/sendsms.php?user=prosyanoy&pwd=jhknlkjn&smsid=$smsid"
    val stringRequest = StringRequest(
        url,
        { response ->
            when (response) {
                "not_deliver" -> Toast.makeText(context, "The message was not delivered", Toast.LENGTH_SHORT)
                "expired" -> Toast.makeText(context, "The subscriber is unavailable right now", Toast.LENGTH_SHORT)
                "deliver" -> Toast.makeText(context, "The message has been sent successfully", Toast.LENGTH_SHORT)
            }
        },
        {
            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT)
        })
    init {
        queue.add(stringRequest)
    }
}