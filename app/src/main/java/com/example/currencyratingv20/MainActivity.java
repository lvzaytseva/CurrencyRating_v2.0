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
import java.util.List;
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

    private List<Currency> currList = new ArrayList<>();
    private static final int LOADER_ID = 1;
    private LoaderManager loaderManager;
    private CurrAdapter adapter;
    private Timer timer;
    private int timerPeriod = 600000;
    private Spinner spinnerFrequency;
    private Spinner spinnerLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerViewCurrency = findViewById(R.id.recyclerViewCurrency);
        adapter = new CurrAdapter(currList);
        recyclerViewCurrency.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCurrency.setAdapter(adapter);

        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        spinnerLimit = findViewById(R.id.spinnerLimit);
        spinnerFrequency.setSelection(10);
        spinnerLimit.setSelection(0);

        loaderManager = LoaderManager.getInstance(this);
        downloadData();
        timer = new Timer();
        UpdateTimerTask timerTask = new UpdateTimerTask();
        timer.schedule(timerTask, timerPeriod, timerPeriod);
    }

    private void downloadData() {
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
        if (currList.isEmpty()) {
            currList = JSONUtils.getDataFromJSON(data, currList);
        } else {
            List<Currency> tempList = new ArrayList<>();
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
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        UpdateTimerTask timerTask = new UpdateTimerTask();
        timer.schedule(timerTask, timerPeriod, timerPeriod);
    }

    private void countDynamics(List<Currency> tempList) {
        for (Currency currency : tempList) {
            if (currList.contains(currency)) {
                int j = currList.indexOf(currency);
                currList.get(j).setDynamics(currency.getPrice().subtract(currList.get(j).getPrice()));
                currList.get(j).setPrice(currency.getPrice());
            } else {
                currList.add(currency);
            }
        }
        if (currList.size() > NetworkUtils.getLimitValue()) {
            currList.subList(NetworkUtils.getLimitValue(), currList.size()).clear();
        }
    }

    public void applyButtonClicked(View view) {
        int freq = (int)spinnerFrequency.getSelectedItemId();

        if (freq == 0) {
            timerPeriod = 30000;
        } else {
            timerPeriod = freq * 60000;
        }
        int limit = Integer.parseInt(spinnerLimit.getSelectedItem().toString());
        NetworkUtils.setLimitValue(limit);
        downloadData();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        UpdateTimerTask timerTask = new UpdateTimerTask();
        timer.schedule(timerTask, timerPeriod, timerPeriod);
    }

    public void resetButtonClicked(View view) {
        spinnerFrequency.setSelection(10);
        spinnerLimit.setSelection(0);
    }

    private class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(MainActivity.this::downloadData);
        }
    }
}