package com.example.testapp;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.testapp.MainActivity.DataBackup;
import static com.example.testapp.MainActivity.DataToDisplay;
import static com.example.testapp.MainActivity.NewDataArrivedFlag;
import static com.example.testapp.MainActivity.SOURCE.GLOBAL;
import static com.example.testapp.MainActivity.SOURCE.LOCAL_PAPS;
import static com.example.testapp.MainActivity.mySource;
import static com.example.testapp.NotApp.CHANNEL_ID;


public class PriceUpdateService extends JobService {
    public static final String TAG = "PriceUpdateService";
    private boolean jobCancelled = false;
    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        Log.d("MainActivity", "Current Timestamp: " + format);
        notificationManager = NotificationManagerCompat.from(this);
        GetDataFromNeste(params);
        return true;
    }

    public void showNotification() { // This function is only called from button onClick as of now.
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("The best price for you sir")
                .setContentText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_money)
                .build();
        notificationManager.notify(1,notification);
    }

    public void GetDataFromNeste(final JobParameters params) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                String url = "https://www.neste.lv/lv/content/degvielas-cenas/";
                switch (mySource){
                    case GLOBAL:
                        url = "https://www.neste.lv/lv/content/degvielas-cenas/";
                        break;
                    case LOCAL_PAPS:
                        url = "http://192.168.2.222/neste/cenas.html";
                        break;
                }
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

                if (DataToDisplay != DataBackup){
                    showNotification();
                }

                Log.d(TAG, "Here's what we've fetched: " + FuelData.get(0) + "   " + FuelData.get(1) + "   " + FuelData.get(2) + "   " + FuelData.get(3));
                NewDataArrivedFlag=true;
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
