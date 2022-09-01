package com.example.myprojectagro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.myprojectagro.helpers.Constants;

public class SettingsActivity extends BaseActivity {

    private EditText getNoCropsEdit;
    private Button saveBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUi();
        setHint();
        setSharedNoCrops();
        cancelSettings();
    }

    private void initUi() {
        getNoCropsEdit = findViewById(R.id.get_no_crops);
        saveBtn = findViewById(R.id.save_settings);
        cancelBtn = findViewById(R.id.cancel_settings);
    }

    private void setHint() {
        int counter = getCounterNoCropsWeather();
        getNoCropsEdit.setText(Integer.toString(counter));
    }

    private void setSharedNoCrops() {
        saveBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            int noCrops = Integer.parseInt(getNoCropsEdit.getText().toString());
            editor.putInt(Constants.SHAREDNOCROPS, noCrops);
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void cancelSettings() {
        cancelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private int getCounterNoCropsWeather() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getInt(Constants.SHAREDNOCROPS, 0);
    }
}