package sbs.pros.parking.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sbs.pros.parking.R


class AuthFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        val buttonNextFragment = view.findViewById<Button>(R.id.button)
        buttonNextFragment.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_carRegistrationFragment)
        })


        return view
    }
}