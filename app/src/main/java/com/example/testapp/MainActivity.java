package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity {
    private TextView textView, textView2;
    private Button button;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        button = (Button) findViewById(R.id.button);

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyPeriodicWork.class, 20, TimeUnit.MINUTES).build();

        WorkManager.getInstance().enqueue(periodicWorkRequest);



        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Content content = new Content();
                content.execute();

            }
        });
    }

    private class Content extends AsyncTask<Void,Void,Void> {
        List<String> DataToDisplay;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textView.setText(DataToDisplay.get(0));
            textView2.setText(DataToDisplay.get(1) + " \n " + DataToDisplay.get(2) + " \n " + DataToDisplay.get(3));
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids){

            DataToDisplay = GetDataFromNeste();

            return null;

        }


        }


    public static List<String> GetDataFromNeste() {

        String url = "https://www.neste.lv/lv/content/degvielas-cenas/";
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return FuelData;
    }

}

