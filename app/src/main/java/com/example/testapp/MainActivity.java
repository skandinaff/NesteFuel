package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textView, textView2, textView3;
    private Button button;
    private static final String TAG = "MainActivity";

    private static List<String> DataToDisplay;

    public static String FuelType;
    public static List<String> DebugData;
    //= "Diesel"; // Do I need it in MainActivity? Should create a better solution to where store fuel Type
    // Also, should consider creating an object instad of using List<String>

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
        textView3 = (TextView) findViewById(R.id.textView3);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Content content = new Content();

                if(FuelType == null) {
                    Toast.makeText(MainActivity.this, "Please, select Fuel Type First!",
                            Toast.LENGTH_LONG).show();

                }else {
                    content.execute();
                }
            }
        });

        radioGroup = findViewById(R.id.radioGroup);

        getDataFromServer = new GetDataFromServer(dbHelper, mDatabase);

        priceUpdateService = new PriceUpdateService();



        jobBegin();


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        //radioGroup.clearCheck(); In SQL
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
                textView2.setText(DataToDisplay.get(1) + " \n" + DataToDisplay.get(2) + " \n" + DataToDisplay.get(3));
            }
            if(DebugData != null) {
                textView3.setText(DebugData.get(0) + "\n" + DebugData.get(1) + "\n" + DebugData.get(2)
                        + "\n" + DebugData.get(3) + "\n" + DebugData.get(4) + "\n" + DebugData.get(5) );
                      //  + "\n" + DebugData.get(6) + "\n" + DebugData.get(7) + "\n" + DebugData.get(8));
            }
            priceUpdateService.setDataPrevious(DataToDisplay);

        }

        @Override
        protected Void doInBackground(Void... voids){

            getDataFromServer.fetch(FuelType);
            DataToDisplay = getDataFromServer.get();

            //DebugData = getDataFromServer.getDebugData();



            return null;
        }
    }

}

