package com.example.currencyratingv20;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Spinner;

import com.example.currencyratingv20.data.Currency;
import com.example.currencyratingv20.utils.JSONUtils;
import com.example.currencyratingv20.utils.NetworkUtils;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URL;
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

    private ArrayMap<Currency, BigDecimal> currencyArrayMap = new ArrayMap<>();
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
        setupUI();
        downloadData();
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);
        RecyclerView recyclerViewCurrency = findViewById(R.id.recyclerViewCurrency);

        adapter = new CurrAdapter(currencyArrayMap);
        recyclerViewCurrency.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCurrency.setAdapter(adapter);

        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        spinnerLimit = findViewById(R.id.spinnerLimit);
        spinnerFrequency.setSelection(10);
        spinnerLimit.setSelection(0);

        loaderManager = LoaderManager.getInstance(this);
    }

    private void downloadData() {
        URL url = NetworkUtils.buildURL();
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
        startTimer();
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        UpdateTimerTask timerTask = new UpdateTimerTask();
        timer.schedule(timerTask, timerPeriod, timerPeriod);
    }

    private class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(MainActivity.this::downloadData);
        }
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        return new NetworkUtils.JSONLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        if (currencyArrayMap.isEmpty()) {
            currencyArrayMap = JSONUtils.getDataFromJSON(data, currencyArrayMap);
        } else {
            ArrayMap<Currency, BigDecimal> tempArrayMap = new ArrayMap<>();
            tempArrayMap = JSONUtils.getDataFromJSON(data, tempArrayMap);
            countDynamics(tempArrayMap);

    /*        List<Map.Entry<Currency, BigDecimal>> list = new ArrayList(currencyArrayMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Currency, BigDecimal>>() {
                @Override
                public int compare(Map.Entry<Currency, BigDecimal> o1, Map.Entry<Currency, BigDecimal> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            }); */
        }

        adapter.notifyDataSetChanged();
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {
    }

    private void countDynamics(ArrayMap<Currency, BigDecimal> tempArrayMap) {
        for (int i = 0; i < tempArrayMap.size(); i++) {
            if (currencyArrayMap.containsKey(tempArrayMap.keyAt(i))) {
                int j = currencyArrayMap.indexOfKey(tempArrayMap.keyAt(i));
                BigDecimal dynamics = tempArrayMap.keyAt(i).getPrice().subtract(currencyArrayMap.keyAt(j).getPrice());
                currencyArrayMap.setValueAt(j, dynamics);
                currencyArrayMap.keyAt(j).setPrice(tempArrayMap.keyAt(i).getPrice());
            } else {
                currencyArrayMap.put(tempArrayMap.keyAt(i), tempArrayMap.valueAt(i));
            }
        }

        if (currencyArrayMap.size() > NetworkUtils.getLimitValue()) {
            for (int i = NetworkUtils.getLimitValue(); i < currencyArrayMap.size(); i++) {
                currencyArrayMap.removeAt(i);
            }
        }
    }

    public void updateButtonClicked(View view) {
        downloadData();
    }
    public void applyButtonClicked(View view) {
        setParams();
        downloadData();
    }

    private void setParams() {
        int freq = (int)spinnerFrequency.getSelectedItemId();
        int limit = Integer.parseInt(spinnerLimit.getSelectedItem().toString());

        timerPeriod = freq * 60000;
        NetworkUtils.setLimitValue(limit);
    }

    public void resetButtonClicked(View view) {
        spinnerFrequency.setSelection(10);
        spinnerLimit.setSelection(0);
    }

}
