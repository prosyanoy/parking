package sbs.pros.parking.landlord_main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_in.*
import sbs.pros.parking.R
import sbs.pros.parking.databinding.FragmentInBinding
import sbs.pros.parking.databinding.FragmentOutBinding
import sbs.pros.parking.utils.viewLifecycleLazy
import java.util.ArrayList

class OutFragment(var outParkers: ArrayList<Parker>) : Fragment(R.layout.fragment_out) {

    private val binding by viewLifecycleLazy { FragmentOutBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recycler_view.layoutManager = LinearLayoutManager(context)

        recycler_view.adapter = RecyclerAdapterOut(outParkers)


    }

}