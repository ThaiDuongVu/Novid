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
import android.widget.Toast;

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

public class CompareActivity extends AppCompatActivity {

    private ArrayList<String> countryList = new ArrayList<>();
    private String[] flags;

    private Spinner spinner1;
    private Spinner spinner2;

    private int country1Index = 0;
    private int country2Index = 0;

    private DisplayElement confirmed;
    private DisplayElement deaths;
    private DisplayElement recovered;

    private RequestQueue requestQueue;

    private Vibrator vibrator;
    private int vibratingDuration = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        requestQueue = Volley.newRequestQueue(this);

        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        confirmed = new DisplayElement((TextView) findViewById(R.id.leftConfirmedTextView), (TextView) findViewById(R.id.rightConfirmedTextView), (TextView) findViewById(R.id.totalConfirmedTextView), (ProgressBar) findViewById(R.id.confirmedProgressBar));
        deaths = new DisplayElement((TextView) findViewById(R.id.leftDeathTextView), (TextView) findViewById(R.id.rightDeathTextView), (TextView) findViewById(R.id.totalDeathTextView), (ProgressBar) findViewById(R.id.deathProgressBar));
        recovered = new DisplayElement((TextView) findViewById(R.id.leftRecoveredTextView), (TextView) findViewById(R.id.rightRecoveredTextView), (TextView) findViewById(R.id.totalRecoveredTextView), (ProgressBar) findViewById(R.id.recoveredProgressBar));

        flags = getResources().getStringArray(R.array.flags);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        fetchData("Countries", 0, 0);
    }

    private void spinnerHandler(Spinner spinner, ArrayList<String> countryList, final int index) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, countryList);
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (index == 1) {
                    country1Index = position;
                } else {
                    country2Index = position;
                }
                vibrator.vibrate(vibratingDuration);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void update(View view) {
        vibrator.vibrate(vibratingDuration);
        fetchData("Compare", country1Index, country2Index);
    }

    private void fetchData(final String scope, final int country1Index, final int country2Index) {
        String url = "https://api.covid19api.com/summary";
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (scope.equals("Countries")) {
                            try {
                                JSONArray dataArray = response.getJSONArray("Countries");
                                int length = dataArray.length();

                                for (int i = 0; i < length; i++) {
                                    countryList.add(flags[i] + " " + dataArray.getJSONObject(i).getString("Country"));
                                }

                                spinnerHandler(spinner1, countryList, 1);
                                spinnerHandler(spinner2, countryList, 2);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                JSONArray dataArray = response.getJSONArray("Countries");

                                JSONObject dataObject1 = dataArray.getJSONObject(country1Index);
                                JSONObject dataObject2 = dataArray.getJSONObject(country2Index);

                                confirmed.updateCompare(dataObject1.getInt("TotalConfirmed"), dataObject2.getInt("TotalConfirmed"), getResources());
                                deaths.updateCompare(dataObject1.getInt("TotalDeaths"), dataObject2.getInt("TotalDeaths"), getResources());
                                recovered.updateCompare(dataObject1.getInt("TotalRecovered"), dataObject2.getInt("TotalRecovered"), getResources());

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(objectRequest);
    }
}
