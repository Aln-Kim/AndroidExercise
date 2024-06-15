package com.example.listapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class NetworkConfig {
    private static final String URL_STRING = "https://fetch-hiring.s3.amazonaws.com/hiring.json";

    public static ArrayList<DataModel> fetchItems() throws Exception {
        ArrayList<DataModel> items = new ArrayList<>();
        URL url = new URL(URL_STRING);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                int listId = jsonObject.getInt("listId");
                String name = jsonObject.optString("name", "");
                if (!name.isEmpty()) {
                    items.add(new DataModel(id, listId, name));
                }
            }
        } finally {
            urlConnection.disconnect();
        }
        return items;
    }
}
