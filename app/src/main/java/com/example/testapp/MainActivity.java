package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import static com.example.testapp.NotApp.CHANNEL_ID;


public class MainActivity extends AppCompatActivity {
    private TextView textView, textView2;
    private Button button, button2;
    private static final String TAG = "MainActivity";
    private NotificationManagerCompat notificationManager;

    public static List<String> DataToDisplay;
    public static List<String> DataBackup = null;

    //ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        //PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyPeriodicWork.class, 20, TimeUnit.MINUTES).build();
        //WorkManager.getInstance().enqueue(periodicWorkRequest);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Content content = new Content();
                content.execute();

            }
        });

        jobBegin();


    }

    public void jobBegin() {
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
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG,"Job cancelled");
    }

    private class Content extends AsyncTask<Void,Void,Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog=new ProgressDialog(MainActivity.this);
            //progressDialog.show();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textView.setText(DataToDisplay.get(0));
            textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));
            //progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids){
             //DataToDisplay = GetDataFromNeste();

            return null;

        }


        }






    public void showNotification(View v) {
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("The best price for you sir")
                .setContentText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_money)
                .build();
        notificationManager.notify(1,notification);
    }

    public boolean DataMatches(){

        if(DataBackup != DataToDisplay){
            DataBackup = DataToDisplay;
            textView.setText(DataToDisplay.get(0));
            textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));
            return false;
        }else{
            return true;
        }
    }

}

