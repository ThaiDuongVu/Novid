package com.thaiduong.novid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    private TextView globalTextView;
    private TextView timeTextView;

    private Vibrator vibrator;
    private int vibratingDuration = 50;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        globalTextView = findViewById(R.id.globalTextView);
        timeTextView = findViewById(R.id.timeTextView);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        fetchData();
        initBannerAd();
    }

    private void initBannerAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void stats(View view) {
        vibrator.vibrate(vibratingDuration);

        Intent statsIntent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(statsIntent);
    }

    public void compare(View view) {
        vibrator.vibrate(vibratingDuration);

        Intent statsIntent = new Intent(getApplicationContext(), CompareActivity.class);
        startActivity(statsIntent);
    }

    private void fetchData() {
        String url = "https://api.covid19api.com/summary";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            globalTextView.setText("🌎 Global confirmed cases: " + response.getJSONObject("Global").getInt("TotalConfirmed"));
                            timeTextView.setText(getResources().getString(R.string.time_text_view) + " " + response.getJSONArray("Countries").getJSONObject(0).getString("Date"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            fetchData();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        fetchData();
                    }
                });
        requestQueue.add(objectRequest);
    }
}
