package com.example.mapsproject;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;

public class rEITTIsufleri extends AppCompatActivity {
    double rangeFlaw = 0.5;
    Boolean manualInput = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        if(getIntent().getExtras()!=null) {
            rangeFlaw = getIntent().getDoubleExtra("RANGEFLAW", rangeFlaw);
            manualInput = getIntent().getBooleanExtra("SWITCH",false);
        }
        Log.d("CR", String.valueOf(rangeFlaw));
        Log.d("CR", String.valueOf(getIntent().getBooleanExtra("SWITCH",false)));

        Button newRouteButton = (Button) findViewById(R.id.newRouteButton);
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        ActionBar actionBar = getSupportActionBar();
        newRouteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MapsActivity.class);
                if(getIntent().getExtras() != null){
                    myIntent.putExtra("RANGEFLAW", rangeFlaw);
                    myIntent.putExtra("SWITCH", manualInput);
                }

                startActivityForResult(myIntent, 0);
            }

        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SettingsActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        actionBar.setTitle("rEITTIsufleri");

    }
}
