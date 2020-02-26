package com.example.testapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.testapp.MainActivity.GetDataFromNeste;


public class MyPeriodicWork extends Worker {



    public MyPeriodicWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    String url = "https://www.neste.lv/lv/content/degvielas-cenas/";
    String title, name, price, place;
    List<String> FuelData = new ArrayList<String>();

    private static final String TAG = "MyPeriodicWork";

    @NonNull
    @Override

    public Result doWork() {

        GetDataFromNeste();


        return null;

    }


}
