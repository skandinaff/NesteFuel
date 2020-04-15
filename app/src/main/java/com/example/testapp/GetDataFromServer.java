package com.example.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.example.testapp.FuelData.*;

public class GetDataFromServer  {


    private final FuelDBHelper dbHelper;
    private final SQLiteDatabase mDataBase;
    private SQLiteDatabase xmDataBase;

    public GetDataFromServer(FuelDBHelper dbHelper, SQLiteDatabase mDataBase){

        this.dbHelper = dbHelper;
        this.mDataBase = mDataBase;
    }

    private static List<String> DataToDisplay;

    static final int GLOBAL = 0;
    static final int LOCAL_PAPS = 1;
    static final int LOCAL_HOME = 2;
    static private int xFUELTYPE = 3;

    //FuelDBHelper dbHelper = new FuelDBHelper(this);

    public List<String> get(){

        return this.DataToDisplay;
    }


    public void fetch(@org.jetbrains.annotations.NotNull String FuelType){



        List<String> urls = new ArrayList<String>();
        urls.add("https://www.neste.lv/lv/content/degvielas-cenas/");
        urls.add("http://192.168.2.222/neste/cenas.html");
        urls.add("http://192.168.123.17/neste/cenas.html");

        switch (FuelType) {
            case "Petrol 95":
                xFUELTYPE = 1;
                break;
            case "Petrol 98":
                xFUELTYPE = 2;
                break;
            case "Diesel":
                xFUELTYPE = 3;
                break;
            case "Diesel PRO":
                xFUELTYPE = 4;
                break;
        }

                String title, name, price, place;
                float fprice;
                List<String> FuelData = new ArrayList<String>();

                try {
                    Document doc = Jsoup.connect(urls.get(GLOBAL)).get();
                    title = doc.title();

                    Elements node = doc.getElementsByClass("even");
                    //price = table.text().toString();

                    Element row = node.select("tr").get(xFUELTYPE);

                    Elements cols = row.select("td");

                    name = cols.get(0).text().toString();
                    price = cols.get(1).text().toString(); // Price in our case is actually FLOAT
                    place = cols.get(2).text().toString();
                    fprice = Float.parseFloat(price);
                    /*
                    DecimalFormat df = new DecimalFormat("#.000");
                    df.format(fprice);
*/
                    ContentValues cv = new ContentValues();
                    cv.put(FuelEntry.COLUMN_NAME, name);
                    cv.put(FuelEntry.COLUMN_PRICE, fprice);
                    cv.put(FuelEntry.COLUMN_PLACE, place);

                    mDataBase.insert(FuelEntry.TABLE_NAME, null, cv);


                    FuelData.add(title);
                    FuelData.add(name);
                    FuelData.add(price);
                    FuelData.add(place);
                    DataToDisplay = FuelData;

                } catch (
                        IOException e) {
                    e.printStackTrace();
                }


    }

}
