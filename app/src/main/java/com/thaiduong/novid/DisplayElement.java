package com.thaiduong.novid;

import android.annotation.SuppressLint;
import android.widget.ProgressBar;
import android.widget.TextView;

class DisplayElement {
    private TextView oldTextView;
    private TextView newTextView;
    private  TextView totalTextView;

    private ProgressBar progressBar;

    DisplayElement(TextView oldTextView, TextView newTextView, TextView totalTextView, ProgressBar progressBar) {
        this.oldTextView = oldTextView;
        this.newTextView = newTextView;
        this.totalTextView = totalTextView;

        this.progressBar = progressBar;
    }

    @SuppressLint("SetTextI18n")
    void update(int newInt, int total) {
        oldTextView.setText("Old: " + (total - newInt));
        newTextView.setText("New: " + newInt);
        totalTextView.setText("Total: " + total);

        progressBar.setMax(total);
        progressBar.setProgress(total - newInt);
    }
}
