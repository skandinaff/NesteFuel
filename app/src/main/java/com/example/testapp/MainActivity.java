package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textView, textView2;
    private Button button;
    private static final String TAG = "MainActivity";
    private static List<String> DataToDisplay;
    public static String FuelType = "Diesel";

    ProgressDialog progressDialog;

    RadioGroup radioGroup;
    RadioButton radioButton;


    public static SQLiteDatabase mDatabase;

    FuelDBHelper dbHelper = new FuelDBHelper(this);

    PriceUpdateService priceUpdateService;
    GetDataFromServer getDataFromServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabase = dbHelper.getWritableDatabase();


        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Content content = new Content();
                content.execute();
            }
        });

        radioGroup = findViewById(R.id.radioGroup);

        getDataFromServer = new GetDataFromServer(dbHelper, mDatabase);

        priceUpdateService = new PriceUpdateService();

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
        //Do I really need this button? Maybe hide it in 3 points menu
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG,"Job cancelled");
    }

    public void CheckPriceNow(View view) {
        //This happens onClick of SHOW PRICE button and updates the UI, but fetch doesn't work here
        //UPDATE This is now depricated, button fetch shows price as well
        //FOR NOW BUTTON DOES NOTHING!!
        Log.d(TAG, "You've pressed a Button button");

        //GetDataFromServer getDataFromServer = new GetDataFromServer(dbHelper, mDatabase);
        //DataToDisplay = getDataFromServer.get();

        //PriceUpdateService priceUpdateService = new PriceUpdateService();
        //priceUpdateService.setDataPrevious(DataToDisplay);
/*
        if(DataToDisplay != null) {
            textView.setText(DataToDisplay.get(0));
            textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));

        }
        */

    }



    public void checkRadioButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        Log.d(TAG,"RadioButton Text is: " + radioButton.getText() );
        FuelType = radioButton.getText().toString();
    }

    private class Content extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Connecting to outer space...");
            progressDialog.show();

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            if(DataToDisplay != null) {
                textView.setText(DataToDisplay.get(0));
                textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));
            }
            priceUpdateService.setDataPrevious(DataToDisplay);

        }

        @Override
        protected Void doInBackground(Void... voids){

            getDataFromServer.fetch(FuelType);
            DataToDisplay = getDataFromServer.get();


            return null;
        }
    }

}

