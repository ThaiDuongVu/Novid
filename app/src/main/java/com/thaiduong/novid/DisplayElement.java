package com.thaiduong.novid;

import android.content.res.Resources;
import android.widget.ProgressBar;
import android.widget.TextView;

class DisplayElement {
    private final TextView leftTextView;
    private final TextView rightTextView;
    private final TextView totalTextView;

    private final ProgressBar progressBar;

    DisplayElement(TextView leftTextView, TextView rightTextView, TextView totalTextView, ProgressBar progressBar) {
        this.leftTextView = leftTextView;
        this.rightTextView = rightTextView;
        this.totalTextView = totalTextView;

        this.progressBar = progressBar;
    }

    void updateStats(int newInt, int total, Resources resources) {
        leftTextView.setText(resources.getString(R.string.old_text_view) + " " + (total - newInt));
        rightTextView.setText(resources.getString(R.string.new_text_view) + " " + newInt);
        totalTextView.setText(resources.getString(R.string.total_text_view) + " " + total);

        progressBar.setMax(total);
        progressBar.setProgress(total - newInt);
    }

    void updateCompare(int compareElement1, int compareElement2, String flag1, String flag2) {
        leftTextView.setText(flag1 + ": " + compareElement1);
        rightTextView.setText(flag2 + ": " + compareElement2);
        totalTextView.setText(flag1 + " + " + flag2 + ": " + (compareElement1 + compareElement2));

        progressBar.setMax(compareElement1 + compareElement2);
        progressBar.setProgress(compareElement1);
    }
}
