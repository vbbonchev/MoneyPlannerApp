package com.example.aser93.organisernewdesign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
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

public class budgetFragment extends Fragment{

    private static final String TAG = "budgetTab";
    private int createdAlready=0;
    private DBHelper eventsDB=new DBHelper(getActivity());
    public CaldroidFragment caldroidFragment = new CaldroidFragment();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_fragment,container,false);
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
        caldroidFragment.setArguments(args);

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                //shows all events on the date in text format on the screen when clicked
                showDateTransactions(date);
            }

            @Override
            public void onChangeMonth(int month, int year) {

            }

            @Override
            public void onLongClickDate(Date date, View view) {

                Intent intent = new Intent(view.getContext(), newTransaction.class);

                //passing date as long
                long dateLong = date.getTime();
                intent.putExtra("DATE_SELECTED_LONG",dateLong);
                startActivity(intent);
            }

            @Override
            public void onCaldroidViewCreated() {

            }

        };
        caldroidFragment.setCaldroidListener(listener);
        paintTransactions(caldroidFragment);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.budgetCalendar, caldroidFragment).commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        paintTransactions(caldroidFragment);

    }

    //shows all Events on the calDroid
    public void paintTransactions(CaldroidFragment caldroidFragment) {
        DBHelper database=new DBHelper(getActivity());

        int greenColor = Color.parseColor("#339933");
        int redColor = Color.parseColor("#ff4f4f");
        int greyColor=Color.parseColor("#666666");



        ArrayList<String[]> resultArray = database.getAllShowableTransactions();

        HashMap<String,Float> datesAndAmmounts=new HashMap<>();
        for(String[] item:resultArray){
            if(!datesAndAmmounts.containsKey(item[0])) {
                float ammount = 0;
                if (item[1].matches(".*\\w.*")) ammount=Float.parseFloat(item[1]);
                datesAndAmmounts.put(item[0],ammount);
            }
            else {
                float ammount = 0;
                if (item[1].matches(".*\\w.*"))ammount=Float.parseFloat(item[1]);
                datesAndAmmounts.put(item[0],datesAndAmmounts.get(item[0])+ammount);
            }
        }

        for (String[] item : resultArray) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date eventDate=new Date();
            try {
                eventDate=sdf.parse(item[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            float ammount=datesAndAmmounts.get(item[0]);
            //Drawable moneyIcon = getResources().getDrawable(R.drawable.money_icon);
            Drawable moneyIcon=writeOnDrawableAndAddPadding(R.drawable.money_icon,ammount,16);
            PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
            if(ammount==0)moneyIcon.mutate().setColorFilter(greyColor,mMode);
            if(ammount>0)moneyIcon.mutate().setColorFilter(greenColor,mMode);
            if(ammount<0)moneyIcon.mutate().setColorFilter(redColor,mMode);
            caldroidFragment.setBackgroundDrawableForDate(moneyIcon,eventDate);
            caldroidFragment.refreshView();

            //transactionsAndDrawables.put(eventDate,moneyIcon);
        }



        //caldroidFragment.setBackgroundDrawableForDates(transactionsAndDrawables);
        caldroidFragment.refreshView();

    }



    public BitmapDrawable writeOnDrawable(int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(9);

        //bm=addWhiteBorder(bm,1);
        //bm=pad(bm,2,2);
        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 0, bm.getHeight(), paint);

        return new BitmapDrawable(bm);
    }

    public BitmapDrawable writeOnDrawableAndAddPadding(int drawableId, Float text,int padding){

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);
        Bitmap outputimage = Bitmap.createBitmap(bm.getWidth() + padding,bm.getHeight() + padding, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);


        String numberAsString = String.format ("%.2f", text);
        if(text>0)numberAsString="+" + numberAsString;
        Canvas canvas = new Canvas(outputimage);
        canvas.drawBitmap(bm, padding/2, padding/2, null);
        canvas.drawText(numberAsString, 3, outputimage.getHeight()-3, paint);

        return new BitmapDrawable(outputimage);
    }

    //shows all events below selected date - gets them from getEvents
    public void showDateTransactions(Date date){
        ListView eventsListView = (ListView) getView().findViewById(R.id.eventsListView);
        final EventsListAdapter eventsListAdapter = new EventsListAdapter(getContext(), R.layout.events_list_item);
        eventsListView.setAdapter(eventsListAdapter);
        DBHelper database=new DBHelper(getActivity());

        // puts items in the list through the adapter
        for(EventsListItem item : database.getShowableTransactions(date)) {
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

                String titleString=title.substring(18);
                String startDateString=title.substring(6,16);
                String startTimeString=title.substring(0,5);
                String ammount=description.substring(9,19);
                String actualDescr=description.substring(19);
                Log.d("dateslog", startTimeString);
                Log.d("dateslog", startDateString);
                Log.d("dateslog", titleString);
                Log.d("dateslog", ammount);
                Log.d("dateslog", actualDescr);


                Intent intent = new Intent(view.getContext(), newTransaction.class);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate= null;
                try {
                    startDate = sdf.parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long dateLong= startDate.getTime();
                intent.putExtra("DATE_SELECTED_LONG",dateLong);
                intent.putExtra("CHANGE_EVENT",true);
                intent.putExtra("START_DATE_STRING",startDateString);
                intent.putExtra("START_TIME_STRING",startTimeString);
                intent.putExtra("AMMOUNT_STRING",ammount);
                intent.putExtra("TITLE_STRING",titleString);
                intent.putExtra("DESCRIPTION_STRING",actualDescr);
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

                String titleString=title.substring(18);
                String startDateString=title.substring(6,16);
                String startTimeString=title.substring(0,5);
                String ammount=description.substring(9,19);
                String actualDescr=description.substring(19);
                Log.d("dateslog", startTimeString);
                Log.d("dateslog", startDateString);
                Log.d("dateslog", titleString);
                Log.d("dateslog", ammount);
                Log.d("dateslog", actualDescr);


                Log.d("dateslog", "deleting from table");

                DBHelper database=new DBHelper(getActivity());
                database.deleteTransaction(titleString,startDateString,startTimeString,ammount,actualDescr);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date startDate= null;
                try {
                    startDate = sdf.parse(startDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                showDateTransactions(startDate);
                paintTransactions(caldroidFragment);
                caldroidFragment.refreshView();
                return true;
            }
        });

    }

    public void createDrawableDateBackground(CaldroidFragment caldroidFragment,Date date){

        Drawable border= getResources().getDrawable(R.drawable.border);
        caldroidFragment.setBackgroundDrawableForDate(border, date);

    }


}
