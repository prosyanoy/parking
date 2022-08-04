package sbs.pros.parking

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest

class SMSActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val code = (1000..9999).random()
        val phone = "3534"

        SMSRequest(applicationContext, code, phone)
    }
}

class SMSRequest (context : Context, code : Int, phone : String) {
    val url = ""
    val stringRequest = StringRequest(
        url,
        { response ->
            if (response.length < 8) {
                Toast.makeText(context, R.string.successful, Toast.LENGTH_SHORT)
            }
        },
        {
            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT)
        })
}