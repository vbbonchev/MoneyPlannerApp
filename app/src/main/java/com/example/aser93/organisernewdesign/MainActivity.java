package com.example.aser93.organisernewdesign;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

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




    }



    private void setupViewPager(ViewPager viewPager){


        sectionsPageAdapter adapter=new sectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new calendarFragment(),"Calendar");
        adapter.addFragment(new scheduleFragment(),"Schedule");
        adapter.addFragment(new budgetFragment(),"Budget");
        viewPager.setAdapter(adapter);
    }

}
