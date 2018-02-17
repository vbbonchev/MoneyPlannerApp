package com.example.aser93.organisernewdesign;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class newEvent extends AppCompatActivity {


    private DBHelper mydb = new DBHelper(this);
    private Calendar calendar;
    private TextView startingDateView;
    private TextView endingDateView;
    private int year, month, day;
    private String answer="";

    Button submitButton;
    EditText titleText;
    EditText descriptionTextView;
    TextView startingTimeView;
    TextView endingTimeView;
    EditText ammountText;
    CheckBox notifyBox;
    CheckBox ammountBox;
    Switch showOnCalendarSwitch;
    Spinner notifyTimeSpinner;
    Spinner repeatSpinner;
    Spinner colorSpinner;
    int dateClickedDate, dateClickedMonth, dateClickedYear, dateClickedHour;
    int hour, minute;
    boolean change=false;
    String changeStartTimeString,changeEndTimeString,changeDateString,changeTitleString,changeDescriptionString;


    public void fromAssetToFile() {
        try {

            // get input stream for text
            InputStream is = getAssets().open("database.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text=new String(buffer);
            String fileName = "dbOnDevice.txt";
            String textToWrite = text;
            FileOutputStream outputStream;

            outputStream = openFileOutput(fileName , Context.MODE_PRIVATE);
            outputStream.write(textToWrite.getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }













    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        getSupportActionBar().setTitle("New event");
        boolean change = getIntent().getBooleanExtra("CHANGE_EVENT",false);


        if(change) {
            changeStartTimeString=getIntent().getStringExtra("START_TIME_STRING");
            changeEndTimeString=getIntent().getStringExtra("END_TIME_STRING");
            changeDateString=getIntent().getStringExtra("START_DATE_STRING");
            changeTitleString=getIntent().getStringExtra("TITLE_STRING");
            changeDescriptionString=getIntent().getStringExtra("DESCRIPTION_STRING");
           // Toast.makeText(getApplicationContext(),"Event change",Toast.LENGTH_SHORT).show();
        }
        submitButton = (Button) findViewById(R.id.submitButton);
        titleText = (EditText) findViewById(R.id.eventTitle);
        startingDateView = (TextView) findViewById(R.id.startDateTextView);
        endingDateView = (TextView) findViewById(R.id.endingDateTextView);
        descriptionTextView = (EditText) findViewById(R.id.descriptionText);
        endingTimeView = (TextView) findViewById(R.id.endingTimeTextView);
        startingTimeView = (TextView) findViewById(R.id.startingTimeTextView);
        notifyBox = (CheckBox) findViewById(R.id.notifyCheckBox);
        showOnCalendarSwitch = (Switch) findViewById(R.id.showOnCalSwitch);
        ammountBox = (CheckBox) findViewById(R.id.budgetCheckBox);
        notifyTimeSpinner = (Spinner) findViewById(R.id.notifyTimeSpinner);
        repeatSpinner = (Spinner) findViewById(R.id.repeatSpinner);
        ammountText = (EditText) findViewById(R.id.ammountText);
        colorSpinner = (Spinner) findViewById(R.id.colorSpinner);


        //sets up color spinner
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.colors, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] array = getApplication().getResources().getStringArray(R.array.colors);
        Spanned[] spannedStrings = new Spanned[array.length];
        for(int i=0; i<array.length; i++){
            spannedStrings[i] = Html.fromHtml(array[i]);
        }
        colorSpinner.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.support_simple_spinner_dropdown_item,spannedStrings));


        //sets up the notifyTimeCyclesSpinner
        ArrayAdapter<CharSequence> notifyAdapter = ArrayAdapter.createFromResource(this,
                R.array.notifyTimeCycles, android.R.layout.simple_spinner_item);
        notifyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notifyTimeSpinner.setAdapter(notifyAdapter);

        //sets up the repeatSpinner
        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(this,
                R.array.repeatCycles, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(repeatAdapter);



        long dateClickedLong = getIntent().getLongExtra("DATE_SELECTED_LONG", -1);
        Date dateClicked = new Date();
        dateClicked.setTime(dateClickedLong);
        dateClickedDate = dateClicked.getDate();
        dateClickedMonth = dateClicked.getMonth() + 1;
        dateClickedYear = dateClicked.getYear() + 1900;
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR);
        if (currentHour <= 12) currentHour = (currentHour + 12) % 24;


        showStartDate(dateClickedYear, dateClickedMonth, dateClickedDate);
        showEndDate(dateClickedYear, dateClickedMonth, dateClickedDate);


        hour = currentHour;
        minute = 0;
        showStartHour(currentHour, 0);
        int newHour = (currentHour + 1) % 24;
        showEndHour(newHour, 0);


        if(change){
            titleText.setText(changeTitleString);
            descriptionTextView.setText(changeDescriptionString);
            startingDateView.setText(changeDateString);
            endingDateView.setText(changeDateString);
            startingTimeView.setText(changeStartTimeString);
            endingTimeView.setText(changeEndTimeString);
        }
//
//        DBHelper thisDB=new DBHelper(this);
//        thisDB.deleteEvents();


    }

    //openers for the dialogs
    public void setStartDate(View view) {
        showDialog(999);
    }

    public void setEndDate(View view) {
        showDialog(998);
    }

    public void setStartHour(View view) {
        showDialog(997);
    }

    public void setEndHour(View view) {
        showDialog(996);
    }

    //creates DatePicker dialogs for startDate and endDate
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myStartDateListener, dateClickedYear, dateClickedMonth - 1, dateClickedDate);
        }
        if (id == 998) {
            return new DatePickerDialog(this,
                    myEndDateListener, dateClickedYear, dateClickedMonth - 1, dateClickedDate);
        }
        if (id == 997) {
            return new TimePickerDialog(this, myStartTimeListener, hour, minute, true);
        }
        if (id == 996) {
            return new TimePickerDialog(this, myEndTimeListener, hour, minute, true);
        }
        return null;
    }

    //operate after setting start date
    private DatePickerDialog.OnDateSetListener myStartDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    //Toast.makeText(getApplicationContext(), "got the date!", Toast.LENGTH_SHORT).show();
                    showStartDate(arg1, arg2 + 1, arg3);
                    showEndDate(arg1, arg2 + 1, arg3);
                }
            };

    //operate after setting end date
    private DatePickerDialog.OnDateSetListener myEndDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    //Toast.makeText(getApplicationContext(), "got the date!", Toast.LENGTH_SHORT).show();
                    showEndDate(arg1, arg2 + 1, arg3);
                }
            };

    private TimePickerDialog.OnTimeSetListener myStartTimeListener = new
            TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    showStartHour(i, i1);
                    int newHour = (i + 1) % 24;
                    showEndHour(newHour, i1);

                }
            };

    private TimePickerDialog.OnTimeSetListener myEndTimeListener = new
            TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    showEndHour(i, i1);

                }
            };


    //shows start date on screen
    private void showStartDate(int year, int month, int day) {

        if (month >= 10 && day >= 10)
            startingDateView.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        if (month < 10 && day >= 10)
            startingDateView.setText(new StringBuilder().append(day).append("/0")
                    .append(month).append("/").append(year));
        if (month >= 10 && day < 10)
            startingDateView.setText(new StringBuilder().append("0").append(day).append("/")
                    .append(month).append("/").append(year));
        if (month < 10 && day < 10)
            startingDateView.setText(new StringBuilder().append("0").append(day).append("/0")
                    .append(month).append("/").append(year));
    }

    //shows end date on screen
    private void showEndDate(int year, int month, int day) {
        if (month >= 10 && day >= 10)
            endingDateView.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        if (month < 10 && day >= 10)
            endingDateView.setText(new StringBuilder().append(day).append("/0")
                    .append(month).append("/").append(year));
        if (month >= 10 && day < 10)
            endingDateView.setText(new StringBuilder().append("0").append(day).append("/")
                    .append(month).append("/").append(year));
        if (month < 10 && day < 10)
            endingDateView.setText(new StringBuilder().append("0").append(day).append("/0")
                    .append(month).append("/").append(year));
    }

    //shows start hour on screen
    private void showStartHour(int hour, int minute) {
        if (hour >= 10 && minute >= 10)
            startingTimeView.setText(new StringBuilder().append(hour).append(":")
                    .append(minute));
        if (minute < 10 && hour >= 10)
            startingTimeView.setText(new StringBuilder().append(hour).append(":0")
                    .append(minute));
        if (minute >= 10 && hour < 10)
            startingTimeView.setText(new StringBuilder().append("0").append(hour).append(":")
                    .append(minute));
        if (minute < 10 && hour < 10)
            startingTimeView.setText(new StringBuilder().append("0").append(hour).append(":0")
                    .append(minute));
    }

    //shows end hour on screen
    private void showEndHour(int hour, int minute) {
        if (hour >= 10 && minute >= 10)
            endingTimeView.setText(new StringBuilder().append(hour).append(":")
                    .append(minute));
        if (minute < 10 && hour >= 10)
            endingTimeView.setText(new StringBuilder().append(hour).append(":0")
                    .append(minute));
        if (minute >= 10 && hour < 10)
            endingTimeView.setText(new StringBuilder().append("0").append(hour).append(":")
                    .append(minute));
        if (minute < 10 && hour < 10)
            endingTimeView.setText(new StringBuilder().append("0").append(hour).append(":0")
                    .append(minute));
    }


    //starts on SubmitButton press
    public void submitData(View view) {


        String title = titleText.getText().toString();
        String startDate = startingDateView.getText().toString();
        String startTime = startingTimeView.getText().toString();
        String endDate = endingDateView.getText().toString();
        String endTime = endingTimeView.getText().toString();
        String description = descriptionTextView.getText().toString();
        String delay=notifyTimeSpinner.getSelectedItem().toString();
        String repeatSelection =repeatSpinner.getSelectedItem().toString();
        String ammount = ammountText.getText().toString();
        String notify = "0";
        String showOnCal = "0";

        ammount = padRight(ammount,10);
        if(getIntent().getBooleanExtra("CHANGE_EVENT",false)){
            DBHelper database=new DBHelper(getApplicationContext());
            Log.d("deleted events", changeTitleString+" "+changeDateString + " " + changeStartTimeString + " " + changeEndTimeString);
           // Toast.makeText(getApplicationContext(),"deleting event",Toast.LENGTH_SHORT).show();
            database.deleteEvent(changeTitleString,changeDateString,changeStartTimeString,changeEndTimeString);
        }
        if(title.equals(""))title="No Title";
        if(description.equals(""))description="Empty description.";
        if (showOnCalendarSwitch.isChecked())showOnCal = "1";
        if (notifyBox.isChecked())notify = "1";

        String colour = colorSpinner.getSelectedItem().toString();
        String hexColour="FFFFFF";
        if(colour.equals("Blue"))hexColour="#1F45FC";
        if(colour.equals("Red"))hexColour="#F62217";
        if(colour.equals("Green"))hexColour="#B1FB17";
        if(colour.equals("Gray"))hexColour="#666666";
        if(colour.equals("Yellow"))hexColour="#FFD801";


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //add database.txt events based on daily/weekly/monthly/yearly repetition
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        if(repeatSelection.equals("Once"))
        {
            mydb.insertEvent(title, startDate, endDate, startTime, endTime, notify, description, showOnCal,hexColour);
            if(ammountBox.isChecked())
                mydb.insertTransaction("pending for: "+title , startDate,ammount,colour,notify, description,showOnCal,"1",startTime);
        }


        if(repeatSelection.equals("Daily")) {
            for(int i=0;i<500;i++) {
                c.add(Calendar.DATE, 1);
                String output = sdf1.format(c.getTime());
                if(notify.equals("1"))setUpNotification(output,startTime,title,description,delay);
                if(ammountBox.isChecked())
                    mydb.insertTransaction("pending for: "+title , output,ammount,colour,notify, description,showOnCal,"1",startTime);
                mydb.insertEvent(title, output, endDate, startTime, endTime, notify, description, showOnCal,hexColour);
                Log.v("repeatedDates", output);
            }
        }
        if(repeatSelection.equals("Weekly")) {
            mydb.insertEvent(title, startDate, endDate, startTime, endTime, notify, description, showOnCal,hexColour);
            for(int i=0;i<144;i++) {
                c.add(Calendar.DATE, 7);
                String output = sdf1.format(c.getTime());
                if(notify.equals("1"))setUpNotification(output,startTime,title,description,delay);
                if(ammountBox.isChecked())
                    mydb.insertTransaction("pending for: "+title , output,ammount,colour,notify, description,showOnCal,"1",startTime);
                mydb.insertEvent(title, output, endDate, startTime, endTime, notify, description, showOnCal,hexColour);
                Log.v("repeatedDates", output);
            }
        }
        if(repeatSelection.equals("Monthly")) {
            mydb.insertEvent(title, startDate, endDate, startTime, endTime, notify, description, showOnCal,hexColour);
            for(int i=0;i<36;i++) {
                c.add(Calendar.MONTH, 1);
                String output = sdf1.format(c.getTime());
                if(notify.equals("1"))setUpNotification(output,startTime,title,description,delay);
                if(ammountBox.isChecked())
                    mydb.insertTransaction("pending for: "+title , output,ammount,colour,notify, description,showOnCal,"1",startTime);
                mydb.insertEvent(title, output, endDate, startTime, endTime, notify, description, showOnCal,hexColour);
                Log.v("repeatedDates", output);
            }
        }
        if(repeatSelection.equals("Yearly")) {

            mydb.insertEvent(title, startDate, endDate, startTime, endTime, notify, description, showOnCal,hexColour);
            for(int i=0;i<10;i++) {

                c.add(Calendar.YEAR, 1);
                String output = sdf1.format(c.getTime());
                if(notify.equals("1"))setUpNotification(output,startTime,title,description,delay);
                if(ammountBox.isChecked())
                    mydb.insertTransaction("pending for: "+title , output,ammount,colour,notify, description,showOnCal,"1",startTime);
                mydb.insertEvent(title, output, endDate, startTime, endTime, notify, description, showOnCal,hexColour);
                Log.v("repeatedDates", output);
            }
        }

        finish();
    }


    public void setUpNotification(String startDate,String startTime,String title,String description,String delay){

        //construct date for notification and call scheduleNotification

        String dateInString = startDate + " " + startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date notificationDateTime=new Date();
        try {
            notificationDateTime = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //add notification for given delay
        scheduleNotification(getNotification("Event " +title+ "starts in 1 minute","Description: " + description),
                notificationDateTime,delay);
    }



    private void scheduleNotification(Notification notification, Date date,String timer) {

        //set up intent and put  notification in it
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //get epoch milliseconds for given date
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear()+1900, date.getMonth(), date.getDate(),
                date.getHours(), date.getMinutes(),0);
        long futureInMillis =calendar.getTimeInMillis();

        //adjust for timer
        if(timer.equals("1 min")) {
            futureInMillis-=60000;
           // Toast.makeText(getApplicationContext(),"notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }
        if(timer.equals("15 mins")){
            futureInMillis-=900000;
           // Toast.makeText(getApplicationContext(),"notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }
        if(timer.equals("1 hour")){
            futureInMillis-=3600000;
           // Toast.makeText(getApplicationContext(),"notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }
        if(timer.equals("1 week")){
            futureInMillis-=604800000;
           // Toast.makeText(getApplicationContext(),"notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }
        if(timer.equals("2 weeks")){
            futureInMillis-=1209600000;
          //  Toast.makeText(getApplicationContext(),"notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }

        //set up alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);

    }


//    String m_Input;
//
//    public synchronized String getInput(final String question)
//    {
//        final Context context=this;
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
//
//
//
//    String showDialog(String question){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(question);
//        // Set up the input
//        final EditText input = new EditText(this);
//    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//        // Set up the buttons
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                answer = input.getText().toString();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//        return answer;
//    }
//    void startQuerying(){
//
//    String isWeekend="yes";
//        //showDialog("Is it a weekday? {yes/no}");
//        String timeofDay= "noon";
//                //getInput("timeofDay {morning,noon,afternoon,evening}");
//
////    while(IsWeekend!="yes"&&IsWeekend!="no"){
////        IsWeekend=showDialog("Is it a weekday? {yes/no}");
////    }
//
//
//        //get environment
//        Environment prologEnv=new Environment();
//        Interpreter interpreter=prologEnv.createInterpreter();
//
//
//
//        String newLinesInDB="\ntime("+timeofDay+")."+
//                "\nweekend(" + isWeekend+").";
//
//        File file = new File(getApplicationContext().getFilesDir(), "dbOnDevice.txt");
//
//        PrintWriter pw = null;
//        try
//        {
//            FileWriter fw = new FileWriter(file,true);
//            pw = new PrintWriter(fw);
//            pw.println(newLinesInDB);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (pw != null) {
//                pw.close();
//            }
//        }
//        try {
//            String dbtext=readFile(file.toString());
//            Log.d("FILETEXT","rows from DB: " + dbtext);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // get the filename relative to the class file
//        prologEnv.ensureLoaded(AtomTerm.get(file.toString()));
//
//
//        List<PrologTextLoaderError> loadingErrors = prologEnv.getLoadingErrors();
//        if (loadingErrors.size() > 0) {
//            Log.d("LOADINGERRORS",loadingErrors.toString());
//            return;
//        }
//
//        //write to DB
//
//
//
//        VariableTerm goalDate = new VariableTerm("date");
//        Term[] args = {goalDate};
//        // Construct the question
//        CompoundTerm goalTerm = new CompoundTerm(AtomTerm.get("event"), args);
//        Interpreter.Goal goal=interpreter.prepareGoal(goalTerm);
//        int rc = 0;
//        //interpreter.stop(goal);
//        try {
//            rc = interpreter.execute(goal);
//
//        //while
//            if
//                (rc == PrologCode.SUCCESS) {
//            rc = interpreter.execute(goal);
//            Term ans = goalTerm.dereference();
//            String answeredDate=ans.toString();
//            if(answeredDate!="date")openNewEvent(answeredDate);
//            Log.d("spec",ans.toString());
//        }
//        } catch (PrologException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//    private Boolean freeDate(){
//
//        return true;
//    }
//
//    private void openNewEvent(String answeredDate){
//        Intent intent = new Intent(getApplicationContext(), newEvent.class);
//        answeredDate=answeredDate.substring(7);
//        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
//        Date startDate= null;
//        Log.d("spec",answeredDate);
//        try {
//            startDate = sdf.parse(answeredDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        long dateLong = startDate.getTime();
//        intent.putExtra("DATE_SELECTED_LONG",dateLong);
//        intent.putExtra("CHANGE_EVENT",true);
//        intent.putExtra("TITLE_STRING","My new Event");
//        startActivityForResult(intent, 0);
//    }

    private Notification getNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.event_alert_notification);
        return builder.build();
    }
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
}


