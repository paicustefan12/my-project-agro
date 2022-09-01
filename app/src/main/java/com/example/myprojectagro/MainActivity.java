package com.example.myprojectagro;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myprojectagro.daos.CropDao;
import com.example.myprojectagro.databases.AppDatabase;
import com.example.myprojectagro.entities.Crop;
import com.example.myprojectagro.helpers.Constants;
import com.example.myprojectagro.models.Coordinates;
import com.example.myprojectagro.receivers.MyReceiver;
import com.example.myprojectagro.services.WeatherIntentService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "main_activity";
    private final Gson gson = new Gson();
    private EditText cropType;
    private EditText fieldSize;
    private EditText fieldLatitude;
    private EditText fieldLongitude;
    private Button clearAllBtn;
    private Button submitBtn;
    private Button getWeatherBtn;
    private AppDatabase db;
    private CropDao cropDao;
    private MyReceiver myReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        initUi();
        clearAllButton();
        initDb();
        insertCrop();
        registerReceiver();
        getWeather(getCounterNoCropsWeather());
    }

    @Override
    protected void onPause() {
        unregisterReceiver();
        super.onPause();
    }

    private void initUi() {
        cropType = findViewById(R.id.crops);
        fieldSize = findViewById(R.id.field_size);
        fieldLatitude = findViewById(R.id.field_latitude);
        fieldLongitude = findViewById(R.id.field_longitude);
        clearAllBtn = findViewById(R.id.clear_all_button);
        submitBtn = findViewById(R.id.submit_button);
        getWeatherBtn = findViewById(R.id.get_weather_btn);
    }

    private void clearAllButton() {
        clearAllBtn.setOnClickListener(v -> {
            clearAll();
        });
    }

    private void clearAll() {
        cropType.getText().clear();
        fieldSize.getText().clear();
        fieldLatitude.getText().clear();
        fieldLongitude.getText().clear();
    }

    private void initDb() {
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AgroCrops").build();
        cropDao = db.cropDao();
    }

    private void insertCrop() {
        submitBtn.setOnClickListener(v -> {
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    String messageText = (String) inputMessage.obj;
                    Toast.makeText(MainActivity.this, messageText, Toast.LENGTH_SHORT).show();

                    clearAll();
                }
            };

            Runnable runnable = () -> {
                Crop crop = new Crop(cropType.getText().toString(), Double.parseDouble(fieldSize.getText().toString()),
                        fieldLatitude.getText().toString(), fieldLongitude.getText().toString());
                cropDao.insertAll(crop);

                Message message = handler.obtainMessage();
                message.obj = "Entry added in database";
                message.sendToTarget();
            };
            Thread thread = new Thread(runnable);
            thread.start();
        });
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void registerReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCASTSEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter);
    }

    private void getWeather(int counter) {
        setWeatherNull();
        getWeatherBtn.setOnClickListener(v -> {
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    List<Crop> crops = (List<Crop>) inputMessage.obj;
                    ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();

                    for (Crop crop:crops) {
                        coordinates.add(new Coordinates(crop.id, crop.fieldLatitude, crop.fieldLongitude));
                    }
                    Intent intent = new Intent(MainActivity.this, WeatherIntentService.class);
                    intent.setAction(Constants.BROADCASTSEND);

                    String coordinatesJson = gson.toJson(coordinates);
                    Log.i(TAG, "Crops: " + coordinatesJson);
                    intent.putExtra(Constants.INTENTEXTRANAME, coordinatesJson);
                    startService(intent);
                }
            };

            Runnable runnable = () -> {
                List<Crop> crops = cropDao.findLimit(counter);

                Message message = handler.obtainMessage();
                message.obj = crops;
                message.sendToTarget();
            };
            Thread thread = new Thread(runnable);
            thread.start();
        });
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    private int getCounterNoCropsWeather() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int counterNoCropsWeather = sharedPref.getInt(Constants.SHAREDNOCROPS, 0);
        Log.i(TAG, "Counter = " + counterNoCropsWeather);
        return counterNoCropsWeather;
    }

    private void setWeatherNull() {
        Runnable runnable = () -> {
            cropDao.updateAll(null);
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}