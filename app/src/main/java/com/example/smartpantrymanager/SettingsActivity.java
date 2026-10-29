package com.example.smartpantrymanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btnSettingsBack).setOnClickListener(v -> finish());

        TextView version = findViewById(R.id.txtAppVersion);
        version.setText("Smart Pantry Manager\nVersion 1.0");
    }
}
