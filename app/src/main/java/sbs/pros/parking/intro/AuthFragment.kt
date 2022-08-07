package sbs.pros.parking.intro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import sbs.pros.parking.MainActivity
import sbs.pros.parking.R


class AuthFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        val openMap = view.findViewById<Button>(R.id.button)
        openMap.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, MainActivity::class.java))
        })


        return view
    }
}