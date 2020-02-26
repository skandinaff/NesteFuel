package com.example.testapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.testapp.MainActivity.DataToDisplay;

public class PriceUpdateService extends JobService {
    public static final String TAG = "PriceUpdateService";
    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        GetDataFromNeste(params);
        return true;
    }

    public void GetDataFromNeste(final JobParameters params) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                //String url = "https://www.neste.lv/lv/content/degvielas-cenas/";
                String url = "http://192.168.2.222/neste/cenas.html"; // Or replace it with your locally hosted version
                String title, name, price, place;
                List<String> FuelData = new ArrayList<String>();

                try {
                    Document doc = Jsoup.connect(url).get();
                    title = doc.title();

                    Elements node = doc.getElementsByClass("even");
                    //price = table.text().toString();
                    Element row = node.select("tr").get(3); // Third row is Diesel price

                    Elements cols = row.select("td");

                    name = cols.get(0).text().toString();
                    price = cols.get(1).text().toString();
                    place = cols.get(2).text().toString();

                    FuelData.add(title);
                    FuelData.add(name);
                    FuelData.add(price);
                    FuelData.add(place);

                    //TODO write comparison with old data to see if anything changed!


                    DataToDisplay = FuelData;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(jobCancelled){
                    return;
                }

                //return FuelData;
                Log.d(TAG, "Here's what we've fetched: " + FuelData.get(0) + "   " + FuelData.get(1) + "   " + FuelData.get(2) + "   " + FuelData.get(3));
                jobFinished(params,false);
            }
        }).start();


    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled");
        jobCancelled = true;
        return true;
    }

}
