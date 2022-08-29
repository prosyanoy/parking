package sbs.pros.parking.landlord_main

import sbs.pros.parking.R
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_in.*
import sbs.pros.parking.databinding.FragmentInBinding
import sbs.pros.parking.utils.viewLifecycleLazy


class InFragment(var inParkers: ArrayList<Parker>) : Fragment(R.layout.fragment_in) {

    private val binding by viewLifecycleLazy { FragmentInBinding.bind(requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(context)

        recycler_view.adapter = RecyclerAdapterIn(inParkers)

    }
}