package com.example.myprojectagro.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myprojectagro.R;
import com.example.myprojectagro.WeatherActivity;
import com.example.myprojectagro.helpers.Constants;
import com.example.myprojectagro.models.Coordinates;
import com.example.myprojectagro.models.WeatherResponseCultureModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherIntentService extends IntentService {

    private final Gson gson = new Gson();

    public WeatherIntentService() {
        super("WeatherIntentService");
    }
    private static final String TAG = "WeatherIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.i(TAG, "onHandleIntent: started + " + intent.getExtras().getString(Constants.INTENTEXTRANAME) + ", action: " + intent.getAction());
            if (intent.getAction() != null && intent.getAction().equals(Constants.BROADCASTSEND)) {
                ArrayList<Coordinates> coordinates = gson.fromJson(intent.getExtras().getString(Constants.INTENTEXTRANAME), new TypeToken<ArrayList<Coordinates>>(){}.getType());
                Log.i(TAG, "onHandleIntent: request started");
                // send request to server -> certificate in res/raw
                Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://192.168.1.21:45458/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

                WeatherService service = retrofit.create(WeatherService.class);
                Call<ArrayList<WeatherResponseCultureModel>> weatherCall = service.getWeather(coordinates);

                weatherCall.enqueue(new Callback<ArrayList<WeatherResponseCultureModel>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<WeatherResponseCultureModel>> call, @NonNull Response<ArrayList<WeatherResponseCultureModel>> response) {
                    Log.i(TAG, "onResponse: Started " + response.body());

                    assert response.body() != null;

                    intent.putExtra(com.example.myprojectagro.helpers.Constants.BROADCASTINTENT, gson.toJson(response.body()));
                    Log.i(TAG, "onResponse: " + gson.toJson(response.body()));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<WeatherResponseCultureModel>> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                    intent.putExtra(Constants.BROADCASTINTENT, "error");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            });
            }
        }
    }
}