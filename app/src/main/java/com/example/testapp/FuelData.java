package com.example.testapp;

import android.provider.BaseColumns;

public class FuelData {

    private FuelData(){

    }

    public static final class FuelEntry implements BaseColumns{
        public static final String TABLE_NAME = "FuelData";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PLACE = "place";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }
}
