package com.example.currencyratingv20.utils;

import com.example.currencyratingv20.data.Currency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    private static final String KEY_RESULTS = "data";
    private static final String KEY_NAME = "name";
    private static final String KEY_QUOTE = "quote";
    private static final String KEY_PRICE = "price";
    private static final String KEY_USD = "USD";

    public static ArrayList<Currency> getDataFromJSON(JSONObject jsonObject, ArrayList<Currency> currList) {

        if (jsonObject == null)
            return currList;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i ++) {
                String name = jsonArray.getJSONObject(i).getString(KEY_NAME);
                double usd = Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject(KEY_QUOTE).getJSONObject(KEY_USD).getString(KEY_PRICE));
                currList.add(new Currency(name,usd));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currList;
    }

}