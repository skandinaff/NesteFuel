package com.example.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

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

import static com.example.testapp.MainActivity.DebugData;

public class GetDataFromServer  {


    private final FuelDBHelper dbHelper;
    private final SQLiteDatabase mDataBase;
    private SQLiteDatabase xmDataBase;

    public GetDataFromServer(FuelDBHelper dbHelper, SQLiteDatabase mDataBase){

        this.dbHelper = dbHelper;
        this.mDataBase = mDataBase;
    }

    private static List<String> DataToDisplay; //, DebugData;


    static final int GLOBAL = 0;
    static final int LOCAL_PAPS = 1;
    static final int LOCAL_HOME = 2;
    static final int LOCAL_HOME_LH = 3;
    static private int xFUELTYPE;// = 3;
    //FuelDBHelper dbHelper = new FuelDBHelper(this);
    public List<String> get(){

        return this.DataToDisplay;
    }
/*
    public List<String> getDebugData(){

        return this.DebugData;
    }
*/

/**
 * This fucntion actually goes and parses the webpage for price changes.
 * It will be called from PriceUpdateService every so often.
 * It will be also called from MainActivity once the button is pressed.
 *
 */
    public void fetch(@org.jetbrains.annotations.NotNull String FuelType, int debugEntriesCount){

        List<String> urls = new ArrayList<String>();
        {
            urls.add("https://www.neste.lv/lv/content/degvielas-cenas/");
            urls.add("http://192.168.2.222/neste/cenas.html");
            urls.add("http://192.168.123.17/neste/cenas.html");
            urls.add("http://localhost/neste/cenas.html");
        }

        switch (FuelType) {
            case "Neste Futura 95":
                xFUELTYPE = 1;
                break;
            case "Neste Futura 98":
                xFUELTYPE = 2;
                break;
            case "Neste Futura D":
                xFUELTYPE = 3;
                break;
            case "Neste Pro Diesel":
                xFUELTYPE = 4;
                break;
            default:
                xFUELTYPE = 0;
                break;
        }
        String title, name, price, place;
        float fprice;
        List<String> FuelData = new ArrayList<String>();
            try {
                Document doc = Jsoup.connect(urls.get(GLOBAL)).get();
                title = doc.title();
                Elements node = doc.getElementsByClass("even");
                Element row = node.select("tr").get(xFUELTYPE);
                Elements cols = row.select("td");
                name = cols.get(0).text().toString();
                price = cols.get(1).text().toString(); // Price in our case is actually FLOAT
                place = cols.get(2).text().toString();
                fprice = Float.parseFloat(price);
                ContentValues cv = new ContentValues();
                cv.put(FuelEntry.COLUMN_NAME, name);
                cv.put(FuelEntry.COLUMN_PRICE, fprice);
                cv.put(FuelEntry.COLUMN_PLACE, place);
                mDataBase.insert(FuelEntry.TABLE_NAME, null, cv);
                if(debugEntriesCount != 0) DebugData = dbHelper.getLastFuelData(FuelType,debugEntriesCount); // Does this really need to be here ???
                // String operations
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
