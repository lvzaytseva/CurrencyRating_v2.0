package com.example.currencyratingv20.utils;

import android.util.ArrayMap;

import com.example.currencyratingv20.data.Currency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class JSONUtils {

    private static final String KEY_RESULTS = "data";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_QUOTE = "quote";
    private static final String KEY_PRICE = "price";
    private static final String KEY_USD = "USD";

    public static ArrayMap<Currency, BigDecimal> getDataFromJSON(JSONObject jsonObject, ArrayMap<Currency, BigDecimal> currencyArrayMap) {
        if (jsonObject == null) {
            return currencyArrayMap;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i ++) {
                int id = Integer.parseInt(jsonArray.getJSONObject(i).getString(KEY_ID));
                String name = jsonArray.getJSONObject(i).getString(KEY_NAME);
                BigDecimal usd = new BigDecimal(jsonArray.getJSONObject(i).getJSONObject(KEY_QUOTE).getJSONObject(KEY_USD).getString(KEY_PRICE));
                currencyArrayMap.put(new Currency(id, name, usd), null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currencyArrayMap;
    }

}
