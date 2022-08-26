package sbs.pros.parking.landlord_main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import sbs.pros.parking.R;

public class InFragment extends Fragment {

    List<Parker> inParkers = new ArrayList<Parker>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in, container, false);

        inParkers.add(new Parker("A 123 BC 45", "12:30", 3));



        return view;
    }
}
