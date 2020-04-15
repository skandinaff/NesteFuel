package com.example.testapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.testapp.FuelData.*;

import androidx.annotation.Nullable;

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
        db.execSQL("DROP TABLE IF EXISTS" + FuelEntry.TABLE_NAME);
        onCreate(db);
    }
}