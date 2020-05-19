package com.thaiduong.novid;

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

    private String url = "https://api.covid19api.com/summary";
    private RequestQueue requestQueue;

    private TextView totalConfirmedTextView;
    private TextView oldConfirmedTextView;
    private TextView newConfirmedTextView;

    private ProgressBar confirmedProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        totalConfirmedTextView = findViewById(R.id.totalConfirmedTextView);
        oldConfirmedTextView = findViewById(R.id.oldConfirmedTextView);
        newConfirmedTextView = findViewById(R.id.newConfirmedTextView);

        confirmedProgressBar = findViewById(R.id.confirmedProgressBar);
    }

    public void globalUpdate(View view) {
        fetchData("Global");
    }

    private void fetchData(final String scope) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataObject = response.getJSONObject(scope);

                            int newConfirmed = dataObject.getInt("NewConfirmed");
                            int totalConfirmed = dataObject.getInt("TotalConfirmed");

                            updateConfirmed(newConfirmed, totalConfirmed);
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

    private void updateConfirmed(int newConfirmed, int totalConfirmed) {
        newConfirmedTextView.setText("New: " + newConfirmed);
        totalConfirmedTextView.setText("Total: " + totalConfirmed);

        oldConfirmedTextView.setText("Old: " + (totalConfirmed - newConfirmed));
    }
}
