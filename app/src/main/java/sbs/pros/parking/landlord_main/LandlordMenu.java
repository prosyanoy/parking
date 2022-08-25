package sbs.pros.parking.landlord_main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.tabs.TabLayout;

import sbs.pros.parking.R;
import sbs.pros.parking.landlord_main.ui.home.HomeFragment;

public class LandlordMenu extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private InFragment inFragment = new InFragment();
    private ParkedFragment parkedFragment = new ParkedFragment();
    private OutFragment outFragment = new OutFragment();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landlord_auth, container, false);

        bottomNavigationView = bottomNavigationView.findViewById(R.id.bottom_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()){

                }


            }
        });



        return view;
    }


}
