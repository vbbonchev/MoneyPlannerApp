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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class newTransaction extends AppCompatActivity {

    private DBHelper mydb = new DBHelper(this);




    EditText titleText;
    TextView dateOfTransView;
    EditText ammountText;
    Spinner repeatSpinner;
    CheckBox notifyBox;
    Spinner notifyTimeSpinner;
    Spinner colorSpinner;
    Switch showOnCalendarSwitch;
    Switch showOnBudgSwitch;
    EditText descriptionTextView;
    Button submitButton;
    TextView timeOfTransView;
    boolean change=false;
    String changeStartTimeString,changeAmmountString,changeDateString,changeTitleString,changeDescriptionString;

    int dateClickedDate, dateClickedMonth, dateClickedYear, dateClickedHour;
    int hour, minute;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
        getSupportActionBar().setTitle("New Transaction");


        titleText = (EditText) findViewById(R.id.transactionTitle);
        dateOfTransView = (TextView) findViewById(R.id.transactionDate);
        timeOfTransView = (TextView) findViewById(R.id.startingTimeTextView);
        descriptionTextView = (EditText) findViewById(R.id.descriptionText);
        ammountText = (EditText) findViewById(R.id.transactionAmmount);
        notifyBox = (CheckBox) findViewById(R.id.notifyCheckBox);
        showOnCalendarSwitch = (Switch) findViewById(R.id.showOnCalSwitch);
        showOnBudgSwitch = (Switch) findViewById(R.id.showOnBudgetSwitch);
        notifyTimeSpinner = (Spinner) findViewById(R.id.notifyTimeSpinner);
        repeatSpinner = (Spinner) findViewById(R.id.repeatSpinner);
        submitButton = (Button) findViewById(R.id.submitButton);
        colorSpinner = (Spinner) findViewById(R.id.colorSpinner);



        change = getIntent().getBooleanExtra("CHANGE_EVENT", false);
        if(change) {
            changeStartTimeString=getIntent().getStringExtra("START_TIME_STRING");
            changeDateString=getIntent().getStringExtra("START_DATE_STRING");
            changeTitleString=getIntent().getStringExtra("TITLE_STRING");
            changeDescriptionString=getIntent().getStringExtra("DESCRIPTION_STRING");
            changeAmmountString=getIntent().getStringExtra("AMMOUNT_STRING");
           // Toast.makeText(getApplicationContext(),"Transaction change",Toast.LENGTH_SHORT).show();
        }

        //sets up color spinner
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.colors, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //colorSpinner.setAdapter(colorAdapter);

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
       
        hour=currentHour;
        showStartDate(dateClickedYear, dateClickedMonth, dateClickedDate);
        showStartHour(currentHour, 0);

        if(change){
            titleText.setText(changeTitleString);
            descriptionTextView.setText(changeDescriptionString);
            dateOfTransView.setText(changeDateString);
            ammountText.setText(changeAmmountString);
            timeOfTransView.setText(changeStartTimeString);
        }





//
//        DBHelper thisDB=new DBHelper(this);
//        thisDB.deleteTransactions();


    }

    //openers for the dialogs
    public void setStartDate(View view) {
        showDialog(999);
    }

    public void setStartHour(View view) {
        showDialog(997);
    }



    //creates DatePicker dialogs for startDate and endDate
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myStartDateListener, dateClickedYear, dateClickedMonth - 1, dateClickedDate);
        }
        if (id == 997) {
            return new TimePickerDialog(this, myTransactionTimeListener, hour, minute, true);
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

                }
            };



    private TimePickerDialog.OnTimeSetListener myTransactionTimeListener = new
            TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    showStartHour(i, i1);
                    int newHour = (i + 1) % 24;

                }
            };




    //shows start date on screen
    private void showStartDate(int year, int month, int day) {

        if (month >= 10 && day >= 10)
            dateOfTransView.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));
        if (month < 10 && day >= 10)
            dateOfTransView.setText(new StringBuilder().append(day).append("/0")
                    .append(month).append("/").append(year));
        if (month >= 10 && day < 10)
            dateOfTransView.setText(new StringBuilder().append("0").append(day).append("/")
                    .append(month).append("/").append(year));
        if (month < 10 && day < 10)
            dateOfTransView.setText(new StringBuilder().append("0").append(day).append("/0")
                    .append(month).append("/").append(year));
    }



    //shows start hour on screen
    private void showStartHour(int hour, int minute) {
        if (hour >= 10 && minute >= 10)
            timeOfTransView.setText(new StringBuilder().append(hour).append(":")
                    .append(minute));
        if (minute < 10 && hour >= 10)
            timeOfTransView.setText(new StringBuilder().append(hour).append(":0")
                    .append(minute));
        if (minute >= 10 && hour < 10)
            timeOfTransView.setText(new StringBuilder().append("0").append(hour).append(":")
                    .append(minute));
        if (minute < 10 && hour < 10)
            timeOfTransView.setText(new StringBuilder().append("0").append(hour).append(":0")
                    .append(minute));
    }



    //starts on SubmitButton press
    public void submitData(View view) {


        String title = titleText.getText().toString();
        String dateOfTrans = dateOfTransView.getText().toString();
        String description = descriptionTextView.getText().toString();
        String timeOfTrans = timeOfTransView.getText().toString();
        String delay=notifyTimeSpinner.getSelectedItem().toString();
        String repeatSelection =repeatSpinner.getSelectedItem().toString();
        String ammount = ammountText.getText().toString();

        String colour = colorSpinner.getSelectedItem().toString();
        String hexColour="#FFFFFF";
        if(colour.equals("Blue"))hexColour="#1F45FC";
        if(colour.equals("Red"))hexColour="#F62217";
        if(colour.equals("Green"))hexColour="#B1FB17";
        if(colour.equals("Gray"))hexColour="#666666";
        if(colour.equals("Yellow"))hexColour="#FFD801";

        String notify = "0";
        String showOnCal = "0";
        String showOnBudg = "0";
        ammount = padRight(ammount,10);
        if(title.equals(""))title="no title";
        if(description.equals(""))description="empty description";

        if(getIntent().getBooleanExtra("CHANGE_EVENT",false)){
            DBHelper database=new DBHelper(getApplicationContext());
            Log.d("deleted events", changeTitleString+" "+changeDateString + " " + changeStartTimeString + " " + changeAmmountString);
           // Toast.makeText(getApplicationContext(),"deleting event",Toast.LENGTH_SHORT).show();
            database.deleteTransaction(changeTitleString,changeDateString,changeStartTimeString,changeAmmountString,changeDescriptionString);
        }
        if (showOnCalendarSwitch.isChecked())showOnCal = "1";
        if (showOnBudgSwitch.isChecked())showOnBudg = "1";

        if (notifyBox.isChecked())notify = "1";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateOfTrans));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //add database.txt events based on daily/weekly/monthly/yearly repetition
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        if(repeatSelection.equals("Once")) {
           if(notify.equals("1")) setUpNotification(dateOfTrans,timeOfTrans,title,description,delay);
            mydb.insertTransaction(title, dateOfTrans, ammount, hexColour, notify, description, showOnCal, showOnBudg,timeOfTrans);
            if(showOnBudg.equals("1"))
                mydb.insertEvent("Payment for: " + title, dateOfTrans, dateOfTrans, timeOfTrans, timeOfTrans, notify, description, showOnCal,hexColour);
        }
        if(repeatSelection.equals("Daily")) {
            mydb.insertTransaction(title, dateOfTrans,ammount,hexColour , notify, description, showOnCal,showOnBudg,timeOfTrans);
            for(int i=0;i<500;i++) {
                c.add(Calendar.DATE, 1);
                String output = sdf1.format(c.getTime());
                if(notify.equals("1")) setUpNotification(dateOfTrans,timeOfTrans,title,description,delay);
                mydb.insertTransaction(title, output,ammount,hexColour , notify, description, showOnCal,showOnBudg,timeOfTrans);
                if(showOnBudg.equals("1"))
                    mydb.insertEvent("Payment for: " + title, output, output, timeOfTrans, timeOfTrans, notify, description, showOnCal,hexColour);
            }
        }
        if(repeatSelection.equals("Weekly")) {
            mydb.insertTransaction(title, dateOfTrans,ammount,hexColour , notify, description, showOnCal,showOnBudg,timeOfTrans);
            for(int i=0;i<144;i++) {
                c.add(Calendar.DATE, 7);
                String output = sdf1.format(c.getTime());
                if(notify.equals("1")) setUpNotification(dateOfTrans,timeOfTrans,title,description,delay);
                mydb.insertTransaction(title, output,ammount,hexColour , notify, description, showOnCal,showOnBudg,timeOfTrans);
                if(showOnBudg.equals("1"))
                    mydb.insertEvent("Payment for: " + title, output, output, timeOfTrans, timeOfTrans, notify, description, showOnCal,hexColour);
            }
        }
        if(repeatSelection.equals("Monthly")) {
            mydb.insertTransaction(title, dateOfTrans,ammount,hexColour , notify, description, showOnCal,showOnBudg,timeOfTrans);
            for(int i=0;i<36;i++) {
                c.add(Calendar.MONTH, 1);
                String output = sdf1.format(c.getTime());
                if(notify.equals("1")) setUpNotification(dateOfTrans,timeOfTrans,title,description,delay);
                mydb.insertTransaction(title, output,ammount,hexColour , notify, description, showOnCal,showOnBudg,timeOfTrans);
                if(showOnBudg.equals("1"))
                    mydb.insertEvent("Payment for: " + title, output, output, timeOfTrans, timeOfTrans, notify, description, showOnCal,hexColour);
            }
        }
        if(repeatSelection.equals("Yearly")) {

            mydb.insertTransaction(title, dateOfTrans,ammount,hexColour , notify, description, showOnCal,showOnBudg,timeOfTrans);
            for(int i=0;i<10;i++) {

                c.add(Calendar.YEAR, 1);
                String output = sdf1.format(c.getTime());
                if(notify.equals("1")) setUpNotification(dateOfTrans,timeOfTrans,title,description,delay);
                mydb.insertTransaction(title, output,ammount,hexColour , notify, description, showOnCal,showOnBudg,timeOfTrans);
                if(showOnBudg.equals("1"))
                    mydb.insertEvent("Payment for: " + title, output, output, timeOfTrans, timeOfTrans, notify, description, showOnCal,hexColour);
            }
        }


        finish();
    }


    public void setUpNotification(String startDate,String transactionTime,String title,String description,String delay){

        //construct date for notification and call scheduleNotification

        String dateInString = startDate + " " + transactionTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date notificationDateTime=new Date();
        try {
            notificationDateTime = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //add notification for given delay
        scheduleNotification(getNotification("Event " +title+ "starts in "+ delay,"Description: " + description),
                notificationDateTime,delay);
    }



    private void scheduleNotification(Notification notification, Date date, String timer) {

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
           // Toast.makeText(getApplicationContext(),"Notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }
        if(timer.equals("15 mins")){
            futureInMillis-=900000;
           // Toast.makeText(getApplicationContext(),"Notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }
        if(timer.equals("1 hour")){
            futureInMillis-=3600000;
          //  Toast.makeText(getApplicationContext(),"Notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }
        if(timer.equals("1 week")){
            futureInMillis-=604800000;
          //  Toast.makeText(getApplicationContext(),"Notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }
        if(timer.equals("2 weeks")){
            futureInMillis-=1209600000;
          //  Toast.makeText(getApplicationContext(),"Notification set for "+ timer + " before event.",Toast.LENGTH_SHORT).show();
        }

        //set up alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);

    }

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
