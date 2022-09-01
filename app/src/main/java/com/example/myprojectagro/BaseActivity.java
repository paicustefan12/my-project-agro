package com.example.myprojectagro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.crops_view_btn:
                seeCrops();
                return true;
            case R.id.insert_crop_btn:
                insertCrops();
                return true;
            case R.id.settings_btn:
                settings();
                return true;
            case R.id.logout_btn:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void seeCrops() {
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
    }

    private void insertCrops() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, OnboardActivity.class);
        startActivity(intent);
    }
}