package com.thaiduong.novid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void stats(View view) {
        Intent statsIntent = new Intent(getApplicationContext(), StatsActivity.class);
        startActivity(statsIntent);
    }
}
