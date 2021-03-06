package com.thaiduong.novid;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

import static com.thaiduong.novid.MainActivity.countryCodeToEmoji;

public class StatsActivity extends AppCompatActivity {

    // List of countries to display stats for
    private final ArrayList<String> countryList = new ArrayList<>();
    private final int vibratingDuration = 50;
    // Drop down spinner to select country
    private Spinner spinner;
    // Display element for confirmed cases
    private DisplayElement confirmed;
    // Display element for deaths
    private DisplayElement deaths;
    // Display element for recovered cases
    private DisplayElement recovered;
    // Scope of the display element
    private String scope;
    private int scopeIndex;
    private RequestQueue requestQueue;
    // Current device's vibrating functionality
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        requestQueue = Volley.newRequestQueue(this);

        confirmed = new DisplayElement((TextView) findViewById(R.id.oldConfirmedTextView), (TextView) findViewById(R.id.newConfirmedTextView), (TextView) findViewById(R.id.totalConfirmedTextView), (ProgressBar) findViewById(R.id.confirmedProgressBar));
        deaths = new DisplayElement((TextView) findViewById(R.id.oldDeathTextView), (TextView) findViewById(R.id.newDeathTextView), (TextView) findViewById(R.id.totalDeathTextView), (ProgressBar) findViewById(R.id.deathProgressBar));
        recovered = new DisplayElement((TextView) findViewById(R.id.oldRecoveredTextView), (TextView) findViewById(R.id.newRecoveredTextView), (TextView) findViewById(R.id.totalRecoveredTextView), (ProgressBar) findViewById(R.id.recoveredProgressBar));

        spinner = findViewById(R.id.spinner);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Fetch global data on startup
        fetchData(getResources().getString(R.string.global_text_view), 1);
        fetchData("Countries", 1);
    }

    // Handle spinner drop down & items to display
    private void handleSpinner(Spinner spinner, final ArrayList<String> countryList) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, countryList);
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scope = countryList.get(position);
                scopeIndex = position - 1;

                vibrator.vibrate(vibratingDuration);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Update view to display fetched data
    public void update(View view) {
        vibrator.vibrate(vibratingDuration);
        fetchData(scope, scopeIndex);
    }

    // Fetch data from COVID19 API and update text views accordingly
    private void fetchData(final String scope, final int scopeIndex) {
        String url = "https://api.covid19api.com/summary";
        JsonObjectRequest apiRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (scope.equals("Countries")) {
                                JSONArray dataArray = response.getJSONArray("Countries");
                                int length = dataArray.length();

                                countryList.add(getResources().getString(R.string.global_text_view));
                                for (int i = 0; i < length; i++) {
                                    countryList.add(countryCodeToEmoji(dataArray.getJSONObject(i).getString("CountryCode")) + " " + dataArray.getJSONObject(i).getString("Country"));
                                }

                                handleSpinner(spinner, countryList);

                            } else {
                                JSONObject dataObject;

                                // Fetch global data
                                if (scope.equals(getResources().getString(R.string.global_text_view))) {
                                    dataObject = response.getJSONObject("Global");
                                } else {
                                    JSONArray dataArray = response.getJSONArray("Countries");
                                    dataObject = dataArray.getJSONObject(scopeIndex);
                                }

                                // Update display elements based on response object
                                confirmed.updateStats(dataObject.getInt("NewConfirmed"), dataObject.getInt("TotalConfirmed"), getResources());
                                deaths.updateStats(dataObject.getInt("NewDeaths"), dataObject.getInt("TotalDeaths"), getResources());
                                recovered.updateStats(dataObject.getInt("NewRecovered"), dataObject.getInt("TotalRecovered"), getResources());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            // If there's an exception then try fetching data again
                            fetchData(scope, scopeIndex);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there's an error then try fetching data again
                        fetchData(scope, scopeIndex);
                    }
                });

        // Add created request to request queue to start fetching.
        requestQueue.add(apiRequest);
    }
}
