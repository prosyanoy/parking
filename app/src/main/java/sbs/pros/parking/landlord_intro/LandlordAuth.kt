package sbs.pros.parking.landlord_intro

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.*
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentLandlordAuthBinding
import sbs.pros.parking.utils.navigateSafe
import sbs.pros.parking.utils.setSafeOnClickListener
import sbs.pros.parking.utils.viewLifecycleLazy

class LandlordAuth : Fragment(R.layout.fragment_landlord_auth){


    private val binding by viewLifecycleLazy { FragmentLandlordAuthBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.enter.setSafeOnClickListener {
            val inputLogin = binding.login.text.toString()
            val inputPassword = binding.password.text.toString()
            if (inputLogin.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Введите логин и пароль или войдите как гость",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (inputLogin == "admin" && inputPassword == "12345") {
                    findNavController().navigateSafe(R.id.action_landlordAuth_to_landlordMenu)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Неправильный логин или пароль", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.register.setSafeOnClickListener {
            //go to registration menu or smth
        }


    }
}