package sbs.pros.parking.landlord_main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import sbs.pros.parking.R;

public class OutFragment extends Fragment {

    List<Parker> outParkers = new ArrayList<Parker>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_out, container, false);



        return view;
    }
}
