package com.example.testapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GetDataFromServer {

    public static List<String> DataToDisplay;

    private enum SOURCE {
        LOCAL_PAPS,
        GLOBAL
    }
    private SOURCE mySource = SOURCE.LOCAL_PAPS;



    public List<String> get(){

        return this.DataToDisplay;
    }

    public void fetch(){

                String url = "https://www.neste.lv/lv/content/degvielas-cenas/";
                switch (mySource) {
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

                    DataToDisplay = FuelData;


                } catch (
                        IOException e) {
                    e.printStackTrace();
                }


    }

}
