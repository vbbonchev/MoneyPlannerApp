package com.example.aser93.organisernewdesign;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Events.db";
    public static final String EVENTS_TABLE_NAME = "Events";
    public static final String EVENTS_COLUMN_TITLE = "TITLE";
    public static final String EVENTS_COLUMN_STARTDATE = "STARTDATE";
    public static final String EVENTS_COLUMN_ENDDATE = "ENDDATE";
    public static final String EVENTS_COLUMN_STARTTIME= "STARTTIME";
    public static final String EVENTS_COLUMN_ENDTIME = "ENDTIME";
    public static final String EVENTS_COLUMN_NOTIFY = "NOTIFY";
    public static final String EVENTS_COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String EVENTS_COLUMN_SHOWONCAL = "SHOWONCAL";
    public static final String EVENTS_COLUMN_COLOUR = "COLOUR";

    public static final String TRANSACTIONS_TABLE_NAME = "Transactions";
    public static final String TRANSACTIONS_COLUMN_TITLE = "TITLE";
    public static final String TRANSACTIONS_COLUMN_DATEOFTRANS = "DATEOFTRANS";
    public static final String TRANSACTIONS_COLUMN_TIMEOFTRANS = "TIMEOFTRANS";
    public static final String TRANSACTIONS_COLUMN_AMMOUNT = "AMMOUNT";
    public static final String TRANSACTIONS_COLUMN_COLOUR = "COLOUR";
    public static final String TRANSACTIONS_COLUMN_NOTIFY = "NOTIFY";
    public static final String TRANSACTIONS_COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String TRANSACTIONS_COLUMN_SHOWONCAL = "SHOWONCAL";
    public static final String TRANSACTIONS_COLUMN_SHOWONBUDG = "SHOWONBUDG";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Events"+
                "(TITLE TEXT,STARTDATE TEXT,ENDDATE TEXT,STARTTIME TEXT,ENDTIME TEXT,NOTIFY INT,DESCRIPTION TEXT,SHOWONCAL INT,COLOUR TEXT);");
        db.execSQL("CREATE TABLE Transactions"+
                "(TITLE TEXT,DATEOFTRANS TEXT,AMMOUNT TEXT,COLOUR TEXT,NOTIFY INT,DESCRIPTION TEXT,SHOWONCAL INT, SHOWONBUDG INT,TIMEOFTRANS TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Events");
        db.execSQL("DROP TABLE IF EXISTS Transactions");
        onCreate(db);
    }

       public boolean insertEvent (String title, String startdate, String enddate, String starttime,String endtime,String notify,String description,String showoncal,
                                   String colour)
       {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("startdate", startdate);
        contentValues.put("enddate", enddate);
        contentValues.put("starttime", starttime);
        contentValues.put("endtime", endtime);
        contentValues.put("notify", notify);
        contentValues.put("description", description);
        contentValues.put("showoncal", showoncal);
        contentValues.put("colour",colour);
        db.insert("Events", null, contentValues);
        return true;
    }

    public boolean insertTransaction (String title, String dateoftrans, String ammount,String colour,String notify,String description,
                                      String showoncal,String showonbudg,String timeoftrans) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("dateoftrans", dateoftrans);
        contentValues.put("ammount", ammount);
        contentValues.put("colour", colour);
        contentValues.put("notify", notify);
        contentValues.put("description", description);
        contentValues.put("showoncal", showoncal);
        contentValues.put("showonbudg", showonbudg);
        contentValues.put("timeoftrans",timeoftrans);
        db.insert("Transactions", null, contentValues);
        return true;
    }



    public Cursor getData(String startDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where STARTDATE="+startDate+"", null );
        return res;
    }

    public Cursor getDataForEventDate(String dateoftrans){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where STARTDATE="+dateoftrans+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, EVENTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Events");
    }

    public void deleteEvent(String title,String startDate,String startTime,String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete("Events",
                "TITLE=? AND STARTDATE=? AND STARTTIME=? AND ENDTIME=?",
                new String[] {title, startDate, startTime, endTime});
        Log.d("dateslog", Integer.toString(deleted));
    }

    public void deleteTransaction(String title,String dateOfTrans,String timeOfTrans,String ammount,String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete("Transactions",
                "TITLE=? AND DATEOFTRANS=? AND TIMEOFTRANS=? AND AMMOUNT=?",
                new String[] {title, dateOfTrans, timeOfTrans, ammount,});
        Log.d("dateslog", Integer.toString(deleted));
    }

    public void deleteTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Transactions");
    }

    public List<EventsListItem> getShowableEvents(Date date){
        List<EventsListItem> items=new ArrayList<EventsListItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
        String simpleDate = formatter.format(date);


        String[] whereArgs = new String[] {
                simpleDate
        };
        Cursor res = db.query("Events", null,"STARTDATE=?",whereArgs,null,null,"STARTTIME");


        res.moveToFirst();
        while(!res.isAfterLast()){
            String title=res.getString(res.getColumnIndex(EVENTS_COLUMN_TITLE));
            String description=res.getString(res.getColumnIndex(EVENTS_COLUMN_DESCRIPTION));
            String startTime = res.getString(res.getColumnIndex(EVENTS_COLUMN_STARTTIME));
            String endTime = res.getString(res.getColumnIndex(EVENTS_COLUMN_ENDTIME));
            String startDate = res.getString(res.getColumnIndex(EVENTS_COLUMN_STARTDATE));
            String colour = res.getString(res.getColumnIndex(EVENTS_COLUMN_COLOUR));
            title=" " + startTime + "-" + endTime + " " + startDate + ", " + title;
            //Log.d("databaseDump",title+", "+ description);
            EventsListItem eventToAdd=new EventsListItem(title,description,new Date(),colour);
            items.add(eventToAdd);
            res.moveToNext();
        }

        return items;
    }


    public List<EventsListItem> getShowableTransactions(Date date){
        List<EventsListItem> items=new ArrayList<EventsListItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
        String simpleDate = formatter.format(date);


        String[] whereArgs = new String[] {
                simpleDate
        };
        Cursor res = db.query("Transactions", null,"DATEOFTRANS=?",whereArgs,null,null ,"TIMEOFTRANS");


        res.moveToFirst();
        while(!res.isAfterLast()){
            String title=res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_TITLE));
            String description=res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_DESCRIPTION));
            String ammount = res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_AMMOUNT));
            String timeOfTrans = res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_TIMEOFTRANS));
            String dateOfTrans = res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_DATEOFTRANS));
            String colour = res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_COLOUR));

            title =timeOfTrans + " " + dateOfTrans + ", " + title ;
            description = "Ammount: " + ammount+"\n" + description ;
            EventsListItem eventToAdd=new EventsListItem(title,description,new Date(),colour);
            items.add(eventToAdd);
            res.moveToNext();
        }
        return items;
    }

    public ArrayList<String[]> getAllShowableEvents() {
        ArrayList<String[]> array_list = new ArrayList<String[]>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Events WHERE SHOWONCAL=" +"'"+"1"+"'", null );

        res.moveToFirst();

        while(!res.isAfterLast()){
            String[] item=new String[2];
            item[0]=res.getString(res.getColumnIndex(EVENTS_COLUMN_STARTDATE));
            item[1]=res.getString(res.getColumnIndex(EVENTS_COLUMN_TITLE));
            array_list.add(item);
            res.moveToNext();
        }

        return array_list;
    }

    public ArrayList<String[]> getAllShowableTransactions() {
        ArrayList<String[]> array_list = new ArrayList<String[]>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Transactions WHERE SHOWONBUDG=" +"'"+"1"+"'", null );

        res.moveToFirst();

        while(!res.isAfterLast()){
            //Log.v("databaseDump",res.getString((res.getColumnIndex(TRANSACTIONS_COLUMN_DATEOFTRANS))));
            String[] item=new String[2];
            item[0]=res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_DATEOFTRANS));
            item[1]=res.getString(res.getColumnIndex(TRANSACTIONS_COLUMN_AMMOUNT));


            array_list.add(item);
            res.moveToNext();
        }

        return array_list;
    }



    public ArrayList<String> getAllEvents() {
        ArrayList<String> array_list = new ArrayList<String>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Events", null );

        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(EVENTS_COLUMN_STARTDATE)));
            res.moveToNext();
        }

        return array_list;
    }
}
