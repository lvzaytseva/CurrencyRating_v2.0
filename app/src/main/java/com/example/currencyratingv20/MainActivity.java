package com.example.currencyratingv20;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import com.example.currencyratingv20.data.Currency;
import com.example.currencyratingv20.utils.JSONUtils;
import com.example.currencyratingv20.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private RecyclerView recyclerViewCurrency;
    private ArrayList<Currency> currList = new ArrayList<>();
    private static final int LOADER_ID = 1;
    private LoaderManager loaderManager;
    private CurrAdapter adapter;
    private Timer timer;
    private UploadTimerTask timerTask;
    private int timerPeriod = 600000;
    private Spinner spinnerFrequency;
    private Spinner spinnerLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewCurrency = findViewById(R.id.recyclerViewCurrency);
        adapter = new CurrAdapter(currList);
        recyclerViewCurrency.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCurrency.setAdapter(adapter);
        loaderManager = LoaderManager.getInstance(this);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        spinnerLimit = findViewById(R.id.spinnerLimit);
        spinnerFrequency.setSelection(10);
        spinnerLimit.setSelection(0);
        downloadData();
        timer = new Timer();
        timerTask = new UploadTimerTask();
        timer.schedule(timerTask, timerPeriod, timerPeriod);
    }
    private void downloadData()
    {
        URL url = NetworkUtils.buildURL();
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        return new NetworkUtils.JSONLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {

        if (currList.isEmpty())
            currList = JSONUtils.getDataFromJSON(data, currList);
        else {
            ArrayList<Currency> tempList = new ArrayList<>();
            tempList = JSONUtils.getDataFromJSON(data, tempList);
            countDynamics(tempList);
            Collections.sort(currList);
        }
        adapter.notifyDataSetChanged();
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {
    }

    public void updateButtonClicked(View view) {
        downloadData();
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        UploadTimerTask timerTask = new UploadTimerTask();
        timer.schedule(timerTask, timerPeriod, timerPeriod);
    }

    private void countDynamics(ArrayList<Currency> tempList) {
        for (int i = 0; i < tempList.size(); i++ ) {
            if (currList.contains(tempList.get(i))) {
                int j = currList.indexOf(tempList.get(i));
                currList.get(j).setDynamics((String.format("%.4f", tempList.get(i).getPrice() - currList.get(j).getPrice())).replace(',', '.')); // надо избавиться от этого...
                currList.get(j).setPrice(tempList.get(i).getPrice());
            }
            else
                currList.add(tempList.get(i));
        }
        if (currList.size() > NetworkUtils.getLimitValue()) {
            currList.subList(NetworkUtils.getLimitValue(), currList.size()).clear();
        }
    }

    public void applyButtonClicked(View view) {
        int freq = (int)spinnerFrequency.getSelectedItemId();
        if (freq == 0)
            timerPeriod = 30000;
        else
            timerPeriod = freq*60000;
        int limit = Integer.parseInt(spinnerLimit.getSelectedItem().toString());
        NetworkUtils.setLimitValue(limit);
        downloadData();
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        UploadTimerTask timerTask = new UploadTimerTask();
        timer.schedule(timerTask, timerPeriod, timerPeriod);

    }

    public void resetButtonClicked(View view) {
        spinnerFrequency.setSelection(10);
        spinnerLimit.setSelection(0);
    }

    private class UploadTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    downloadData();
                }
            });
        }

    }



}