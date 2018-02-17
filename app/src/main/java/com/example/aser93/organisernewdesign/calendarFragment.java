package com.example.aser93.organisernewdesign;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gnu.prolog.database.PrologTextLoaderError;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.Term;
import gnu.prolog.term.VariableTerm;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;


public class calendarFragment extends Fragment{


    private static final String TAG = "calendarTab";
    private int createdAlready=0;
    private DBHelper eventsDB=new DBHelper(getActivity());
    public CaldroidFragment caldroidFragment = new CaldroidFragment();
    String answer;

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
                startQuerying();
            }

            @Override
            public void onChangeMonth(int month, int year) {

            }

            @Override
            public void onLongClickDate(Date date, View view) {

                generateTransactionDB();
                startTransactionsQuery();
//
//
//
//
//
//                Intent intent = new Intent(view.getContext(), newEvent.class);
//
//                //passing date as long
//
//                long dateLong= date.getTime();
//                intent.putExtra("DATE_SELECTED_LONG",dateLong);
//                startActivity(intent);
            }

            @Override
            public void onCaldroidViewCreated() {

            }

        };
        caldroidFragment.setCaldroidListener(listener);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.calendar1, caldroidFragment).commit();

    }

    void startTransactionsQuery(){
        String doyouhavemoney="yes";
        String willyouhaveenoughleft="yes";
        Integer howmuchdoyouneedit=1;


        //get environment
        Environment prologEnv=new Environment();
        Interpreter interpreter=prologEnv.createInterpreter();



        String newLinesInDB="\ndoyouhavemoney("+doyouhavemoney+")."+
                "\nwill_willyouhaveenoughleft(" + willyouhaveenoughleft+")."+
                "\nhowmuchdoyouneedit(" + howmuchdoyouneedit +").";

        File file = new File(getContext().getFilesDir(), "transactionsDB.txt");

        PrintWriter pw = null;
        try
        {
            FileWriter fw = new FileWriter(file,true);
            pw = new PrintWriter(fw);
            pw.println(newLinesInDB);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        try {
            String dbtext=readFile(file.toString());
            Log.d("FILETEXT","rows from DB: " + dbtext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // get the filename relative to the class file
        prologEnv.ensureLoaded(AtomTerm.get(file.toString()));


        List<PrologTextLoaderError> loadingErrors = prologEnv.getLoadingErrors();
        if (loadingErrors.size() > 0) {
            Log.d("LOADINGERRORS",loadingErrors.toString());
            return;
        }

        //write to DB



        VariableTerm goalDate = new VariableTerm("howmuchdoyouneedit");
        Term[] args = {goalDate};
        // Construct the question
        CompoundTerm goalTerm = new CompoundTerm(AtomTerm.get("transaction"), args);
        Interpreter.Goal goal=interpreter.prepareGoal(goalTerm);
        int rc = 0;
        //interpreter.stop(goal);
        try {
            rc = interpreter.execute(goal);

            //while
            if
                    (rc == PrologCode.SUCCESS) {
                rc = interpreter.execute(goal);
                Term ans = goalTerm.dereference();
                String answeredDate=ans.toString();

                Log.d("spec",ans.toString());
            }
        } catch (PrologException e) {
            e.printStackTrace();
        }


    }





    String alltext=
            "discontiguous(buynow).\n"+
            "discontiguous(buyinaweek).\n"+
            "discontiguous(buyinamonth).\n"+
            "discontiguous(youcantbuyit).\n"+
            "discontiguous(youdontneedit).\n"+
            "discontiguous(doyouhavemoney).\n"+
            "discontiguous(willyouhaveenoughleft).\n"+
            "discontiguous(howmuchdoyouneedit).\n"+
            "transaction(buynow):- doyouhavemoney(yes),\n willyouhaveenoughleft(yes), \nhowmuchdoyouneedit(5). \n" +
            "transaction(buynow): doyouhavemoney(yes),\n willyouhaveenoughleft(yes), \nhowmuchdoyouneedit(4). \n" +
            "\n" +
            "transaction(buyinaweek):- doyouhavemoney(yes),\n willyouhaveenoughleft(no), \nhowmuchdoyouneedit(5).\n" +
            "transaction(buyinaweek):- doyouhavemoney(yes), \nwillyouhaveenoughleft(no),\n howmuchdoyouneedit(4).\n" +
            "transaction(buyinaweek):- doyouhavemoney(yes), \nwillyouhaveenoughleft(yes), \nhowmuchdoyouneedit(3).\n" +
            "transaction(buyinaweek):- doyouhavemoney(yes), \nwillyouhaveenoughleft(yes), \nhowmuchdoyouneedit(2).\n" +
            "\n" +
            "transaction(buyinamonth):-doyouhavemoney(yes),\n willyouhaveenoughleft(no), \nhowmuchdoyouneedit(3).\n" +
            "transaction(buyinamonth):-doyouhavemoney(yes), \nwillyouhaveenoughleft(no), \nhowmuchdoyouneedit(2).\n" +
            "transaction(buyinamonth):-doyouhavemoney(yes),\n willyouhaveenoughleft(no), \nhowmuchdoyouneedit(1).\n" +
            "transaction(buyinamonth):-doyouhavemoney(yes), \nwillyouhaveenoughleft(yes), \nhowmuchdoyouneedit(1).\n" +
            "\n" +
            "transaction(youcantbuyit):- doyouhavemoney(no).\n" +
            "transaction(youdontneedit):- doyouhavemoney(yes),\n willyouhaveenoughleft(no),\n howmuchdoyouneedit(1). \n" +
            "transaction(youdontneedit):- doyouhavemoney(yes),\n willyouhaveenoughleft(no),\n howmuchdoyouneedit(1). \n" +
            "transaction(youdontneedit):- doyouhavemoney(yes),\n willyouhaveenoughleft(no),\n howmuchdoyouneedit(2). \n" +
            "transaction(youdontneedit):- doyouhavemoney(yes), \nwillyouhaveenoughleft(no),\n howmuchdoyouneedit(4).  ";

    private void generateTransactionDB(){

        //write to DB
        PrintWriter pw = null;
        try
        {
            File file = new File(getContext().getFilesDir(), "transactionsDB.txt");
            FileWriter fw = new FileWriter(file);

            pw = new PrintWriter(fw);
            pw.println(alltext);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

    }


    String m_Input;

//    public synchronized String getInput(final String question)
//    {
//        final Context context=getContext();
//        runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle(question);
//                // Set up the input
//                final EditText input = new EditText(context);
//                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(input);
//                // Set up the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        answer = input.getText().toString();
//                        notify();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        notify();
//                    }
//                });
//
//                builder.show();
//            }
//        });
//        try
//        {
//            wait();
//        }
//        catch (InterruptedException e)
//        {
//
//        }
//
//        return m_Input;
//    }



    String showDialog(String question){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(question);
        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                answer = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        return answer;
    }
    void startQuerying(){

        String isWeekend="yes";
        //showDialog("Is it a weekday? {yes/no}");
        String timeofDay= "noon";
        //getInput("timeofDay {morning,noon,afternoon,evening}");

//    while(IsWeekend!="yes"&&IsWeekend!="no"){
//        IsWeekend=showDialog("Is it a weekday? {yes/no}");
//    }


        //get environment
        Environment prologEnv=new Environment();
        Interpreter interpreter=prologEnv.createInterpreter();



        String newLinesInDB="\ntime("+timeofDay+")."+
                "\nweekend(" + isWeekend+").";

        File file = new File(getContext().getFilesDir(), "dbOnDevice.txt");

        PrintWriter pw = null;
        try
        {
            FileWriter fw = new FileWriter(file,true);
            pw = new PrintWriter(fw);
            pw.println(newLinesInDB);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        try {
            String dbtext=readFile(file.toString());
            Log.d("FILETEXT","rows from DB: " + dbtext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // get the filename relative to the class file
        prologEnv.ensureLoaded(AtomTerm.get(file.toString()));


        List<PrologTextLoaderError> loadingErrors = prologEnv.getLoadingErrors();
        if (loadingErrors.size() > 0) {
            Log.d("LOADINGERRORS",loadingErrors.toString());
            return;
        }

        //write to DB



        VariableTerm goalDate = new VariableTerm("date");
        Term[] args = {goalDate};
        // Construct the question
        CompoundTerm goalTerm = new CompoundTerm(AtomTerm.get("event"), args);
        Interpreter.Goal goal=interpreter.prepareGoal(goalTerm);
        int rc = 0;
        //interpreter.stop(goal);
        try {
            rc = interpreter.execute(goal);

            //while
            if
                    (rc == PrologCode.SUCCESS) {
                rc = interpreter.execute(goal);
                Term ans = goalTerm.dereference();
                String answeredDate=ans.toString();
                if(answeredDate!="date")openNewEvent(answeredDate);
                Log.d("spec",ans.toString());
            }
        } catch (PrologException e) {
            e.printStackTrace();
        }


    }

    private String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }

    private Boolean freeDate(){

        return true;
    }

    private void openNewEvent(String answeredDate){
        Intent intent = new Intent(getContext(), newEvent.class);
        answeredDate=answeredDate.substring(7);
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date startDate= null;
        Log.d("spec",answeredDate);
        try {
            startDate = sdf.parse(answeredDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateLong = startDate.getTime();
        intent.putExtra("DATE_SELECTED_LONG",dateLong);
        intent.putExtra("CHANGE_EVENT",true);
        intent.putExtra("TITLE_STRING","My new Event");
        startActivityForResult(intent, 0);
    }


}
