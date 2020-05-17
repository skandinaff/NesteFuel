package com.example.testapp;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.testapp.FuelData.*;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

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
                "); ";
        db.execSQL(SQL_CREATE_FUELDATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FuelEntry.TABLE_NAME);
        onCreate(db);
    }
    /**
    * This function gets last fuel data form DB.
    */
    public List<String> getLastFuelData(String fueltype, int entriesCount){
        List<String> FuelData = new ArrayList<String>();
        String name = fueltype;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FuelEntry.TABLE_NAME, new String[]{
                        FuelEntry.COLUMN_NAME,
                        FuelEntry.COLUMN_PRICE,
                        FuelEntry.COLUMN_PLACE,
                        FuelEntry.COLUMN_TIMESTAMP
                },
                FuelEntry.COLUMN_NAME+"=?", new String[]{fueltype},null,null,null);

        long fuelTypeCount = DatabaseUtils.queryNumEntries(db,FuelEntry.TABLE_NAME,FuelEntry.COLUMN_NAME + " = '" + name+"'");//,new String[] {name});
        if (cursor != null) cursor.moveToLast();
        if (entriesCount > fuelTypeCount) entriesCount = (int)fuelTypeCount; // Checking if we haven't requested more entries than there are in DB
        for (int i=0;i<=entriesCount;i++){

            if(i==0 && entriesCount == 0) { // Special case for comparing in background process.
                cursor.moveToPrevious();
                FuelData.add("Degvielas cenas | Neste");
                FuelData.add(cursor.getString(0)); // Place
                FuelData.add(cursor.getString(1)); // Price
                FuelData.add(cursor.getString(2)); // Timestamp
            }
            if(i==0 && entriesCount != 0){
                FuelData.add(cursor.getString(1)); // Price
                FuelData.add(cursor.getString(2)); // Place
                FuelData.add(cursor.getString(3)); // Timestamp
            }
            else if (i !=0 && cursor.moveToPrevious())
            {

                if(DataPointsComparison(FuelData,cursor)==true) //Last value in the FuelData != cursor getString()
                {
                    FuelData.add(cursor.getString(1)); // Price
                    FuelData.add(cursor.getString(2)); // Place
                    FuelData.add(cursor.getString(3)); // Timestamp
                }
                else i--;
            }
        }
        return FuelData; // Need to return entriesCount as well, so we can reduce
    }
    private boolean DataPointsComparison(List<String> DataPrev, Cursor cursorToData){
        /* Just for debugging.
        String price = DataPrev.get(DataPrev.size() - 3);
        String place = DataPrev.get(DataPrev.size() - 2);
        String pricePrev = cursorToData.getString(1);
        String placePrev = cursorToData.getString(2);
        */
        if((DataPrev.get(DataPrev.size() - 3)).equals(cursorToData.getString(1))
        ||  (DataPrev.get(DataPrev.size() - 2)).equals(cursorToData.getString(2))){
            return false;
        } else return true;

    }


}
