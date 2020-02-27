package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.app.Notification;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.testapp.NotApp.CHANNEL_ID;


public class MainActivity extends AppCompatActivity {
    private TextView textView, textView2;
    private Button button;
    private static final String TAG = "MainActivity";

    //private NotificationManagerCompat notificationManager;

    public static List<String> DataToDisplay;
    public static List<String> DataBackup = null;
    public static boolean NewDataArrivedFlag = false;

    public enum SOURCE {
        LOCAL_PAPS,
        GLOBAL
    }
    public static SOURCE mySource = SOURCE.LOCAL_PAPS;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //notificationManager = NotificationManagerCompat.from(this);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        button = (Button) findViewById(R.id.button);
        //button2 = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Content content = new Content();
                content.execute();
            }
        });

        jobBegin();





    }

    public void jobBegin() { //The job being was unassigned from button to run it automatically.
        ComponentName componentName = new ComponentName(this, PriceUpdateService.class);
        JobInfo info = new JobInfo.Builder(123,componentName)
                .setPersisted(true)
                .setPeriodic(15*60*1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultcode = scheduler.schedule(info);
        if(resultcode==JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job schedulling failed");
        }
    }

    public void jobCancel(View view) {
        //TODO Do I really need this button? Maybe hide it in 3 points menu
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG,"Job cancelled");
    }

    private class Content extends AsyncTask<Void,Void,Void> {
        //TODO Decide wheter this class is necessary for updating the TextViews
        // EVERYTHING that happens here is onClick

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progressDialog=new ProgressDialog(MainActivity.this);
            //progressDialog.setMessage("Checking price");
            //progressDialog.show();

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(DataToDisplay != null) {
                textView.setText(DataToDisplay.get(0));
                textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));
            }
                //progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids){
            GetDataFromNesteNow();
             //DataToDisplay = GetDataFromNeste();



            if (NewDataArrivedFlag && DataToDisplay != DataBackup) {
                //showNotification();
                NewDataArrivedFlag=false;
            }

            return null;

        }


        }

/*
    public void showNotification() { // This function is only called from button onClick as of now.
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("The best price for you sir")
                .setContentText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_money)
                .build();
        notificationManager.notify(1,notification);
    }
*/
    public boolean DataReady(){ // ???

        if(DataBackup != null){
            textView.setText(DataToDisplay.get(0));
            textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));
            return true;
        }else{
            return false;
        }
    }

    public void GetDataFromNesteNow(){
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
    }

}

