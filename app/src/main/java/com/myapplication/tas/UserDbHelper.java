package com.myapplication.tas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by criqulau on 23/12/2015.
 * Database manipulation helper class.
 */
public class UserDbHelper extends SQLiteOpenHelper {

    // Create a databasefor the application if it doesn't exist or edit the actual one.
    // The database created/ modified is directly linked with the DATABASE_VERSION parameter.
    private static final String DATABASE_NAME = "TASEVENTS.DB";
    private static final int DATABASE_VERSION = 1;
    // !!!!!!! The query string has to be formated like this with space at the correct positions!!!!
    private static final String CREATE_QUERY =
            "CREATE TABLE " + DatabaseContainer.NewDatabasInfos.TABLE_NAME + "(" +DatabaseContainer.NewDatabasInfos.EVENT_NAME + " TEXT," + DatabaseContainer.NewDatabasInfos.EVENT_TIME + " TEXT," +
                    DatabaseContainer.NewDatabasInfos.EVENT_IMAGE_PATH + " TEXT," + DatabaseContainer.NewDatabasInfos.EVENT_DESCRIPTION + " TEXT,"+ DatabaseContainer.NewDatabasInfos.PHP_LOGIN + " TEXT," +
                    DatabaseContainer.NewDatabasInfos.PHP_PASSWORD + " TEXT," + DatabaseContainer.NewDatabasInfos.EVENT_ACTIVE + " INTEGER,"+ DatabaseContainer.NewDatabasInfos.SPARE_1 + " TEXT," +
                    DatabaseContainer.NewDatabasInfos.SPARE_2 + " TEXT," + DatabaseContainer.NewDatabasInfos.SPARE_3 + " TEXT,"+
                    DatabaseContainer.NewDatabasInfos.SPARE_4 + " TEXT,"+ DatabaseContainer.NewDatabasInfos.SPARE_5 + " TEXT);";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATION", "Database created/ opened...");
    }

    @Override
    // Call if the database version doesn't exist only.
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATION", "Table created...");
    }

    // Function to add a row in the database.
    public void addInformations(String event_name, String event_time, String event_image_path, String event_description, String php_login, String php_password, int event_active, String spare_1,String spare_2,String spare_3,String spare_4,String spare_5, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContainer.NewDatabasInfos.EVENT_NAME, event_name);
        contentValues.put(DatabaseContainer.NewDatabasInfos.EVENT_TIME, event_time);
        contentValues.put(DatabaseContainer.NewDatabasInfos.EVENT_IMAGE_PATH, event_image_path);
        contentValues.put(DatabaseContainer.NewDatabasInfos.EVENT_DESCRIPTION, event_description);
        contentValues.put(DatabaseContainer.NewDatabasInfos.PHP_LOGIN, php_login);
        contentValues.put(DatabaseContainer.NewDatabasInfos.PHP_PASSWORD, php_password);
        contentValues.put(DatabaseContainer.NewDatabasInfos.EVENT_ACTIVE, event_active);
        contentValues.put(DatabaseContainer.NewDatabasInfos.SPARE_1, spare_1);
        contentValues.put(DatabaseContainer.NewDatabasInfos.SPARE_2, spare_2);
        contentValues.put(DatabaseContainer.NewDatabasInfos.SPARE_3, spare_3);
        contentValues.put(DatabaseContainer.NewDatabasInfos.SPARE_4, spare_4);
        contentValues.put(DatabaseContainer.NewDatabasInfos.SPARE_5, spare_5);
        db.insert(DatabaseContainer.NewDatabasInfos.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATION", "One row inserted...");
    }

    public Cursor getInformations(SQLiteDatabase db)
    {
        // Function to get all the infos in the database, return a cursor with all the info -> To parse.
        Cursor cursor;
        String[] projections ={DatabaseContainer.NewDatabasInfos.EVENT_NAME,DatabaseContainer.NewDatabasInfos.EVENT_TIME,DatabaseContainer.NewDatabasInfos.EVENT_IMAGE_PATH,DatabaseContainer.NewDatabasInfos.EVENT_DESCRIPTION,
                DatabaseContainer.NewDatabasInfos.PHP_LOGIN,DatabaseContainer.NewDatabasInfos.PHP_PASSWORD,DatabaseContainer.NewDatabasInfos.EVENT_ACTIVE,DatabaseContainer.NewDatabasInfos.SPARE_1,
                DatabaseContainer.NewDatabasInfos.SPARE_2,DatabaseContainer.NewDatabasInfos.SPARE_3,DatabaseContainer.NewDatabasInfos.SPARE_4,DatabaseContainer.NewDatabasInfos.SPARE_5};
        cursor=db.query(DatabaseContainer.NewDatabasInfos.TABLE_NAME,projections,null, null, null, null, null);
        return cursor;
    }

    public void deleteInfo(String event_to_delete, SQLiteDatabase db){

        // Function to delete a row with a particular id in the database -> Search on the id in the id column and delete.
        String selection=DatabaseContainer.NewDatabasInfos.EVENT_NAME+" LIKE ?";
        String [] selection_args={event_to_delete};
        db.delete(DatabaseContainer.NewDatabasInfos.TABLE_NAME, selection,selection_args);
        Log.e("DATABASE OPERATION", "One row deleted...");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}



