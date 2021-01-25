package com.example.mapsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    double rangeFlaw = 0.5;

    Button exitButton;
    Switch manualInput;
    TextView currentFlaw;

    List<Button> buttons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        exitButton = (Button) findViewById(R.id.backButton);
        manualInput = (Switch) findViewById(R.id.manualInput);
        currentFlaw = (TextView) findViewById(R.id.rangeFlawInfo2);

        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), rEITTIsufleri.class);
                myIntent.putExtra("RANGEFLAW", rangeFlaw);
                myIntent.putExtra("SWITCH", manualInput.isChecked());
                startActivityForResult(myIntent, 0);
            }

        });


        for (int i = 1; i < 7; i++) {
            final int id = getResources().getIdentifier("button"+i, "id",getPackageName());
            findViewById(id).setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    Log.d("CR","clicked " + id);
                    Button button=(Button)findViewById(id);
                    String btnText=String.valueOf(button.getText());
                    rangeFlaw = Double.valueOf(btnText.replace(" km", ""));
                    Log.d("CR",String.valueOf(rangeFlaw));
                    currentFlaw.setText("Current: " + rangeFlaw);
                }
            });
            Log.d("CR", String.valueOf(i));
        }

        currentFlaw.setText("Current: " + rangeFlaw);
    }
}
