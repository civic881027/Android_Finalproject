package com.example.android_finalproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbOpenHelper extends SQLiteOpenHelper {


    public dbOpenHelper(Context context){
        super(context,ContractLocation.DATABASE_NAME,null,ContractLocation.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATION_TABLE="CREATE TABLE IF NOT EXISTS "+ContractLocation.Location.TABLE_NAME
                + "("+ContractLocation.Location.ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +ContractLocation.Location.LONG+" REAL NOT NULL,"
                +ContractLocation.Location.LAT+" REAL NOT NULL,"
                +ContractLocation.Location.NAME+" TEXT NOT NULL);";

        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE="DROP TABLE IF EXISTS "+ContractLocation.Location.TABLE_NAME;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
