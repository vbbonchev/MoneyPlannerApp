package com.example.aser93.organisernewdesign;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends FragmentActivity {

    static final String TAG = "MainActivity";

    private sectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectionsPageAdapter=new sectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //initializeCalendar();
        generatePrologDb();





    }

    public String insertDateIntoPrologDB(Calendar cal){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateRepresentation=dateFormat.format(cal.getTime());
        String weekend;
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek==1||dayOfWeek==7)weekend="weekend(yes)";
        else weekend="weekend(no)";

        //m n a e
        //...

        String rowsToInsert="event(" + dateRepresentation +"m):-\n"+
                //...
                "time(morning),\n"+
                weekend + ".\n";
        rowsToInsert=rowsToInsert+"event(" + dateRepresentation +"n):-\n"+
                //...
                "time(noon),\n"+
                weekend + ".\n";
        rowsToInsert=rowsToInsert+"event(" + dateRepresentation +"a):-\n"+
                //...
                "time(afternoon),\n"+
                weekend + ".\n";
        rowsToInsert=rowsToInsert+"event(" + dateRepresentation +"e):-\n"+
                //...
                "time(evening),\n"+
                weekend + ".\n";
        return rowsToInsert;

    }

    public void generatePrologDb(){
        String fullDbAsString="";
        int NUM_OF_DAYS_FORWARD=90;
        Calendar rightNow=Calendar.getInstance();
        insertDateIntoPrologDB(rightNow);
        for(int i=0;i<NUM_OF_DAYS_FORWARD;i++){
            fullDbAsString=fullDbAsString+"\n" + insertDateIntoPrologDB(rightNow);
            rightNow.add(Calendar.DATE,1);
        }



        //write to DB
        PrintWriter pw = null;
        try
        {
            File file = new File(getApplicationContext().getFilesDir(), "dbOnDevice.txt");
            FileWriter fw = new FileWriter(file);
            pw = new PrintWriter(fw);
            pw.println(fullDbAsString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

    }



    private void setupViewPager(ViewPager viewPager){


        sectionsPageAdapter adapter=new sectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new calendarFragment(),"Calendar");
        adapter.addFragment(new scheduleFragment(),"Schedule");
        adapter.addFragment(new budgetFragment(),"Budget");
        viewPager.setAdapter(adapter);
    }

}
