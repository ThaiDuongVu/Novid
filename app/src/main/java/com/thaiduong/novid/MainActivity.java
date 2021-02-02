package com.thaiduong.novid;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final int vibratingDuration = 50;
    // Text to display global stats
    private TextView globalTextView;
    // Text to display time of retrieval
    private TextView timeTextView;

    private RequestQueue requestQueue;

    // Current device's vibrating functionality
    private Vibrator vibrator;

    // Covert a country code string to an emoji that can be displayed on text views
    public static String countryCodeToEmoji(String countryCode) {
        int firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6;
        int secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6;
        return new String(Character.toChars(firstLetter)) + new String(Character.toChars(secondLetter));
    }

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

    // Initialize banner advertisement
    private void initBannerAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = findViewById(R.id.adView);

        // Create a new ad request
        AdRequest adRequest = new AdRequest.Builder().build();
        // Load created ad request to display it
        adView.loadAd(adRequest);
    }

    // Load the 'Stats' activity
    public void stats(View view) {
        vibrator.vibrate(vibratingDuration);

        Intent statsIntent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(statsIntent);
    }

    // Load the 'Compare' activity
    public void compare(View view) {
        vibrator.vibrate(vibratingDuration);

        Intent statsIntent = new Intent(getApplicationContext(), CompareActivity.class);
        startActivity(statsIntent);
    }

    // Fetch data from COVID19 API and update text views accordingly
    private void fetchData() {
        String url = "https://api.covid19api.com/summary";
        JsonObjectRequest apiRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Update text views based on response object
                            globalTextView.setText("🌎 Global confirmed cases: " + response.getJSONObject("Global").getInt("TotalConfirmed"));
                            timeTextView.setText(getResources().getString(R.string.time_text_view) + " " + response.getJSONArray("Countries").getJSONObject(0).getString("Date"));
                        } catch (JSONException e) {
                            e.printStackTrace();

                            // If there's an exception then try fetching data again
                            fetchData();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there's an error then try fetching data again
                        fetchData();
                    }
                });

        // Add created request to request queue to start fetching.
        requestQueue.add(apiRequest);
    }
}
