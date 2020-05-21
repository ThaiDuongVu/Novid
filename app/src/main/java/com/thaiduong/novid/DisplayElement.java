package com.thaiduong.novid;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    void updateStats(int newInt, int total, Resources resources) {
        leftTextView.setText(resources.getString(R.string.old_text_view) + " " + (total - newInt));
        rightTextView.setText(resources.getString(R.string.new_text_view) + " " + newInt);
        totalTextView.setText(resources.getString(R.string.total_text_view) + " " + total);

        progressBar.setMax(total);
        progressBar.setProgress(total - newInt);
    }

    @SuppressLint("SetTextI18n")
    void updateCompare(int compareElement1, int compareElement2, Resources resources) {
        leftTextView.setText(compareElement1 + "");
        rightTextView.setText(compareElement2 + "");
        totalTextView.setText(resources.getString(R.string.total_text_view) + " " + (compareElement1 + compareElement2));

        progressBar.setMax(compareElement1 + compareElement2);
        progressBar.setProgress(compareElement1);
    }
}
