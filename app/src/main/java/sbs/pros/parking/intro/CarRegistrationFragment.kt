package sbs.pros.parking.intro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import sbs.pros.parking.MainActivity
import sbs.pros.parking.R


class CarRegistrationFragment : Fragment() {

    private var prefs: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val view : View = inflater.inflate(R.layout.fragment_car_registration, container, false)

        prefs = activity?.getSharedPreferences("sbs.pros.parking", AppCompatActivity.MODE_PRIVATE)

        val openMap = view.findViewById<Button>(R.id.buttonSkipReg)
        openMap.setOnClickListener(View.OnClickListener {
            prefs?.edit()?.putBoolean("firstrun", false)?.commit();

            startActivity(Intent(context, MainActivity::class.java))
        })

        return view
    }

}