package com.example.testapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetDataFromServer {

    private static List<String> DataToDisplay;

    final int GLOBAL = 0;
    final int LOCAL_PAPS = 1;
    final int LOCAL_HOME = 2;

    public List<String> get(){

        return this.DataToDisplay;
    }

    public void fetch(){

        List<String> urls = new ArrayList<String>();
        urls.add("https://www.neste.lv/lv/content/degvielas-cenas/");
        urls.add("http://192.168.2.222/neste/cenas.html");
        urls.add("http://192.168.123.17/neste/cenas.html");


                String title, name, price, place;
                List<String> FuelData = new ArrayList<String>();

                try {
                    Document doc = Jsoup.connect(urls.get(LOCAL_HOME)).get();
                    title = doc.title();

                    Elements node = doc.getElementsByClass("even");
                    //price = table.text().toString();
                    Element row = node.select("tr").get(2); // Third row is Diesel price //TODO make a constant to select fuel type

                    Elements cols = row.select("td");

                    name = cols.get(0).text().toString();
                    price = cols.get(1).text().toString();
                    place = cols.get(2).text().toString();

                    FuelData.add(title);
                    FuelData.add(name);
                    FuelData.add(price);
                    FuelData.add(place);
                    DataToDisplay = FuelData;

                    //TODO implement saving last FuelData into the SharedPreferences or database

                } catch (
                        IOException e) {
                    e.printStackTrace();
                }


    }

}
