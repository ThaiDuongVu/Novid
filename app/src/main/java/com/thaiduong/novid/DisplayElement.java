package com.thaiduong.novid;

import android.annotation.SuppressLint;
import android.widget.ProgressBar;
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

class DisplayElement {
    private TextView leftTextView;
    private TextView rightTextView;
    private TextView totalTextView;

    private ProgressBar progressBar;

    DisplayElement(TextView leftTextView, TextView rightTextView, TextView totalTextView, ProgressBar progressBar) {
        this.leftTextView = leftTextView;
        this.rightTextView = rightTextView;
        this.totalTextView = totalTextView;

        this.progressBar = progressBar;
    }

    @SuppressLint("SetTextI18n")
    void updateStats(int newInt, int total) {
        leftTextView.setText("Old: " + (total - newInt));
        rightTextView.setText("New: " + newInt);
        totalTextView.setText("Total: " + total);

        progressBar.setMax(total);
        progressBar.setProgress(total - newInt);
    }

    @SuppressLint("SetTextI18n")
    void updateCompare(int compareElement1, int compareElement2) {
        leftTextView.setText(compareElement1 + "");
        rightTextView.setText(compareElement2 + "");

        progressBar.setMax(compareElement1 + compareElement2);
        progressBar.setProgress(compareElement1);
    }
}
