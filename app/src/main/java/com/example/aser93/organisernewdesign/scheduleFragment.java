package com.example.aser93.organisernewdesign;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by aser93 on 15.6.2017 г..
 */

public class scheduleFragment extends Fragment{

    private static final String TAG = "scheduleTab";
    private int createdAlready=0;
    private DBHelper eventsDB=new DBHelper(getActivity());
    public CaldroidFragment caldroidFragment = new CaldroidFragment();
   // final ListView eventsListView = (ListView) getView().findViewById(R.id.eventsListView);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_fragment, container, false);
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
        caldroidFragment.setArguments(args);


        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                //shows all events on the date in text format on the screen when clicked
                showDateEvents(date);
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
        paintEvents(caldroidFragment);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.calendar1, caldroidFragment).commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        paintEvents(caldroidFragment);

    }

    //shows all Events on the calDroid
    public void paintEvents(CaldroidFragment caldroidFragment) {
        HashMap eventsAndDrawables = new HashMap<Date, Drawable>();
        DBHelper database=new DBHelper(getActivity());


        int COLOR2 = Color.parseColor("#7ec0ff");
        PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
        Drawable border = getResources().getDrawable(R.mipmap.calendar_event_bigger);
        border.setColorFilter(COLOR2,mMode);
        ArrayList<String[]> resultArray = database.getAllShowableEvents();
        Log.v("EventDateLogger", resultArray.toString());

        for (String[] item : resultArray) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date eventDate=new Date();
            try {
                eventDate=sdf.parse(item[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            eventsAndDrawables.put(eventDate,border);
        }
       // Toast.makeText(getContext(),"refreshed view",Toast.LENGTH_SHORT).show();
        if(!eventsAndDrawables.isEmpty())caldroidFragment.setBackgroundDrawableForDates(eventsAndDrawables);
        caldroidFragment.refreshView();

    }

    //shows all events below selected date - gets them from getEvents
    public void showDateEvents(Date date){
        final ListView eventsListView = (ListView) getView().findViewById(R.id.eventsListView);
        final EventsListAdapter eventsListAdapter = new EventsListAdapter(getContext(), R.layout.events_list_item);
        eventsListView.setAdapter(eventsListAdapter);
        DBHelper database=new DBHelper(getActivity());
        // Populate the list, through the adapter
        for(EventsListItem item : database.getShowableEvents(date)) {
            eventsListAdapter.add(item);
        }
        //click on event - gets the data from the selected field and opens a newEvent windows with the info
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                    EventsListItem item = (EventsListItem) parent.getItemAtPosition(position);
                    String title=item.getTitle();
                    String description=item.getDescription();
                    Log.d("dateslog", title + "  " + description);

                    String titleString=title.substring(25);
                    String startDateString=title.substring(13,23);
                    String startTimeString=title.substring(1,6);
                    String endTimeString=title.substring(7,12);
                    Log.d("dateslog", startTimeString);
                    Log.d("dateslog", endTimeString);
                    Log.d("dateslog", startDateString);
                    Log.d("dateslog", titleString);
                    Log.d("dateslog", description);

                    Intent intent = new Intent(view.getContext(), newEvent.class);

                    DBHelper database=new DBHelper(getActivity());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date startDate= null;
                    try {
                        startDate = sdf.parse(startDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long dateLong= startDate.getTime();
                    intent.putExtra("CHANGE_EVENT",true);
                    intent.putExtra("DATE_SELECTED_LONG",dateLong);
                    intent.putExtra("START_DATE_STRING",startDateString);
                    intent.putExtra("START_TIME_STRING",startTimeString);
                    intent.putExtra("END_TIME_STRING",endTimeString);
                    intent.putExtra("TITLE_STRING",titleString);
                    intent.putExtra("DESCRIPTION_STRING",description);
                    startActivityForResult(intent, 0);

            }

        });

        eventsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                EventsListItem item = (EventsListItem) parent.getItemAtPosition(position);
                String title=item.getTitle();
                String description=item.getDescription();
                Log.d("dateslog", title + "  " + description);
                String titleString=title.substring(25);
                String startDateString=title.substring(13,23);
                String startTimeString=title.substring(1,6);
                String endTimeString=title.substring(7,12);


                Log.d("dateslog", "deleting from table");

                DBHelper database=new DBHelper(getActivity());
                database.deleteEvent(titleString,startDateString,startTimeString,endTimeString);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate= null;
                try {
                    startDate = sdf.parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                showDateEvents(startDate);
                paintEvents(caldroidFragment);
                return true;
            }
        });

    }

    public void createDrawableDateBackground(CaldroidFragment caldroidFragment,Date date){

        Drawable border= getResources().getDrawable(R.drawable.border);
        caldroidFragment.setBackgroundDrawableForDate(border, date);

    }




}
