package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
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
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TextView textView, textView2;
    private Button button;
    private static final String TAG = "MainActivity";

    //private NotificationManagerCompat notificationManager;

    private static List<String> DataToDisplay;



    //GetDataFromServer getDataFromServer = new GetDataFromServer();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //notificationManager = NotificationManagerCompat.from(this);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        button = (Button) findViewById(R.id.button);

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
                .setPeriodic(1*60*1000)
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

    public void CheckPriceNow(View view) { // Now we can update the UI here, but can't fetch the changes form server
        //DataToDisplay = getDataFromServer.get();
        Log.d(TAG, "You've pressed a Button button");

        GetDataFromServer getDataFromServer = new GetDataFromServer();
        //getDataFromServer.fetch();


        DataToDisplay = getDataFromServer.get();
        if(DataToDisplay != null) {
            textView.setText(DataToDisplay.get(0));
            textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));
        }


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
                //progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids){

            GetDataFromServer getDataFromServer = new GetDataFromServer();
            getDataFromServer.fetch();
            DataToDisplay = getDataFromServer.get();



/*
            if(DataToDisplay != null) {
                textView.setText(DataToDisplay.get(0));
                textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));
            }
            if (NewDataArrivedFlag && DataToDisplay != DataBackup) {
                NewDataArrivedFlag=false;
            }
*/
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



}

