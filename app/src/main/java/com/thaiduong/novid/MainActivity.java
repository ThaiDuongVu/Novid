package com.thaiduong.novid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    private DisplayElement confirmed;
    private DisplayElement deaths;
    private DisplayElement recovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        confirmed = new DisplayElement((TextView) findViewById(R.id.oldConfirmedTextView),(TextView) findViewById(R.id.newConfirmedTextView),(TextView) findViewById(R.id.totalConfirmedTextView),(ProgressBar) findViewById(R.id.confirmedProgressBar));
        deaths = new DisplayElement((TextView) findViewById(R.id.oldDeathTextView),(TextView) findViewById(R.id.newDeathTextView),(TextView) findViewById(R.id.totalDeathTextView),(ProgressBar) findViewById(R.id.deathProgressBar));
        recovered = new DisplayElement((TextView) findViewById(R.id.oldRecoveredTextView),(TextView) findViewById(R.id.newRecoveredTextView),(TextView) findViewById(R.id.totalRecoveredTextView),(ProgressBar) findViewById(R.id.recoveredProgressBar));

        fetchData("Global");
    }

    public void update(View view) {
        fetchData("Global");
    }

    private void fetchData(final String scope) {
        String url = "https://api.covid19api.com/summary";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataObject = response.getJSONObject(scope);

                            int newConfirmed = dataObject.getInt("NewConfirmed");
                            int totalConfirmed = dataObject.getInt("TotalConfirmed");

                            int newDeath = dataObject.getInt("NewDeaths");
                            int totalDeaths = dataObject.getInt("TotalDeaths");

                            int newRecovered = dataObject.getInt("NewRecovered");
                            int totalRecovered = dataObject.getInt("TotalRecovered");

                            confirmed.update(newConfirmed, totalConfirmed);
                            deaths.update(newDeath, totalDeaths);
                            recovered.update(newRecovered, totalRecovered);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
