package com.example.testapp;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
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


import static com.example.testapp.NotApp.CHANNEL_ID;


public class PriceUpdateService extends JobService {
    public static final String TAG = "PriceUpdateService";
    private boolean jobCancelled = false;
    private NotificationManagerCompat notificationManager;
    private List<String> DataToDisplay;
    private List<String> DataPrevious;



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

    public void showNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("The best price for you sir")
                .setContentText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_money)
                .build();
        notificationManager.notify(1,notification);
    }

    public void GetDataFromNeste(final JobParameters params) {

        final GetDataFromServer getDataFromServer = new GetDataFromServer();

        new Thread(new Runnable(){
            @Override
            public void run() {

                getDataFromServer.fetch();

                DataToDisplay = getDataFromServer.get();


                Log.d(TAG, "Here's what we've fetched: " + DataToDisplay.get(0) + "   " + DataToDisplay.get(1) + "   " + DataToDisplay.get(2) + "   " + DataToDisplay.get(3));

                if(DataToDisplay != DataPrevious){
                    showNotification();
                }

                DataPrevious = DataToDisplay;

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
