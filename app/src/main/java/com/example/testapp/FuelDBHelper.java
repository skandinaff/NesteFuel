package com.example.testapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.testapp.FuelData.*;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FuelDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "fueldata.db";
    public static final int DATABASE_VERSION = 1;

    public FuelDBHelper(@Nullable Context context) { // @Nullable was not in the example
        super(context, DATABASE_NAME, null  , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FUELDATA_TABLE = "CREATE TABLE " +
                FuelEntry.TABLE_NAME + " (" +
                FuelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FuelEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FuelEntry.COLUMN_PLACE + " TEXT NOT NULL, " +
                FuelEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                FuelEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_FUELDATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FuelEntry.TABLE_NAME);
        onCreate(db);
    }

    public List<String> getLastFuelData(int howmany){
        List<String> FuelData = new ArrayList<String>();
        String name = "Neste Futura D";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FuelEntry.TABLE_NAME, new String[]{FuelEntry.COLUMN_NAME,FuelEntry.COLUMN_PRICE,FuelEntry.COLUMN_PLACE,FuelEntry.COLUMN_TIMESTAMP},
                                    FuelEntry.COLUMN_NAME+"=?", new String[]{name},null,null,null);

        if (cursor != null) cursor.moveToLast();

        for (int i=0;i<howmany;i++){
            if(i != 0) cursor.moveToPrevious();
            //FuelData.add(cursor.getString(2));
            FuelData.add(cursor.getString(3));
            FuelData.add(cursor.getString(1));
        }

        return FuelData;


    }



}
