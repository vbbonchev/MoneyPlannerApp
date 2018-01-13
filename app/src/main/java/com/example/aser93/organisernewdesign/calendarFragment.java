package com.example.aser93.organisernewdesign;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;


public class calendarFragment extends Fragment{


    private static final String TAG = "calendarTab";
    private int createdAlready=0;
    private DBHelper eventsDB=new DBHelper(getActivity());
    public CaldroidFragment caldroidFragment = new CaldroidFragment();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.calendar_fragment,container,false);
        if(createdAlready==0)createCalDroid(caldroidFragment);


        return view;
    }

    //creates the calcDroid, sets it up and displays it
    public void createCalDroid(CaldroidFragment caldroidFragment){
        createdAlready=1;
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefault);
        //args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        //args.putBoolean("enableSwipe", false);
        caldroidFragment.setArguments(args);



        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
            }

            @Override
            public void onChangeMonth(int month, int year) {

            }

            @Override
            public void onLongClickDate(Date date, View view) {

                Intent intent = new Intent(view.getContext(), newEvent.class);

                //passing date as long

                long dateLong= date.getTime();
                intent.putExtra("DATE_SELECTED_LONG",dateLong);
                startActivity(intent);
            }

            @Override
            public void onCaldroidViewCreated() {

            }

        };
        caldroidFragment.setCaldroidListener(listener);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.calendar1, caldroidFragment).commit();

    }


}
