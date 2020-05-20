package com.thaiduong.novid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    private DisplayElement confirmed;
    private DisplayElement deaths;
    private DisplayElement recovered;

    private Spinner spinner;

    private String scope;
    private int scopeIndex;

    private ArrayList<String> countryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        requestQueue = Volley.newRequestQueue(this);

        confirmed = new DisplayElement((TextView) findViewById(R.id.oldConfirmedTextView), (TextView) findViewById(R.id.newConfirmedTextView), (TextView) findViewById(R.id.totalConfirmedTextView), (ProgressBar) findViewById(R.id.confirmedProgressBar));
        deaths = new DisplayElement((TextView) findViewById(R.id.oldDeathTextView), (TextView) findViewById(R.id.newDeathTextView), (TextView) findViewById(R.id.totalDeathTextView), (ProgressBar) findViewById(R.id.deathProgressBar));
        recovered = new DisplayElement((TextView) findViewById(R.id.oldRecoveredTextView), (TextView) findViewById(R.id.newRecoveredTextView), (TextView) findViewById(R.id.totalRecoveredTextView), (ProgressBar) findViewById(R.id.recoveredProgressBar));

        spinner = findViewById(R.id.spinner);
        countryList = new ArrayList<>();

        fetchData("Global", 0);
        fetchData("Countries", 0);
    }

    private void spinnerHandler(Spinner spinner, final ArrayList<String> countryList) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, countryList);
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scope = countryList.get(position);
                scopeIndex = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void update(View view) {
        fetchData(scope, scopeIndex);
    }

    private void fetchData(final String scope, final int scopeIndex) {
        String url = "https://api.covid19api.com/summary";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (scope.equals("Countries")) {
                                JSONArray dataArray = response.getJSONArray("Countries");
                                int length = dataArray.length();

                                countryList.add("Global");
                                for (int i = 0; i < length; i++) {
                                    countryList.add(dataArray.getJSONObject(i).getString("Country"));
                                }

                                spinnerHandler(spinner, countryList);
                            } else {
                                JSONObject dataObject;

                                if (scope.equals("Global")) {
                                    dataObject = response.getJSONObject(scope);
                                } else {
                                    JSONArray dataArray = response.getJSONArray("Countries");
                                    dataObject = dataArray.getJSONObject(scopeIndex);
                                }

                                confirmed.updateStats(dataObject.getInt("NewConfirmed"), dataObject.getInt("TotalConfirmed"));
                                deaths.updateStats(dataObject.getInt("NewDeaths"), dataObject.getInt("TotalDeaths"));
                                recovered.updateStats(dataObject.getInt("NewRecovered"), dataObject.getInt("TotalRecovered"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();

                            fetchData(scope, scopeIndex);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                        fetchData(scope, scopeIndex);
                    }
                });
        requestQueue.add(objectRequest);
    }
}
