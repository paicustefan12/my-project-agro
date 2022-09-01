package com.example.myprojectagro.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.example.myprojectagro.MainActivity;
import com.example.myprojectagro.R;
import com.example.myprojectagro.WeatherActivity;
import com.example.myprojectagro.daos.CropDao;
import com.example.myprojectagro.databases.AppDatabase;
import com.example.myprojectagro.entities.Crop;
import com.example.myprojectagro.helpers.Constants;
import com.example.myprojectagro.models.Coordinates;
import com.example.myprojectagro.models.WeatherResponseCultureModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {

    private final Gson gson = new Gson();
    private AppDatabase db;
    private CropDao cropDao;
    private static final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getExtras().getString(Constants.BROADCASTINTENT).equals("error")) {
                Toast.makeText(context, "Could not retrieve weather", Toast.LENGTH_SHORT).show();
                return;
            }
            // get data from api response
            ArrayList<WeatherResponseCultureModel> weathers = gson.fromJson(intent.getExtras().getString(Constants.BROADCASTINTENT), new TypeToken<ArrayList<WeatherResponseCultureModel>>(){}.getType());
            initDb(context);

            // update db with the daily weather
            for (WeatherResponseCultureModel weather:weathers) {
                // can have more than one per day
                StringBuilder conditions = new StringBuilder();
                for (String condition:weather.Condition) {
                    conditions.append(condition);
                    conditions.append(" ");
                }
                Runnable runnable = () -> cropDao.updateById(conditions.toString(), weather.Id);
                Thread thread = new Thread(runnable);
                thread.start();
            }

            // create notification with intent to start activity weather
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
            intent = new Intent(context, WeatherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("WeatherResponse", intent.getStringExtra(Constants.BROADCASTINTENT));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_add_alert)
                    .setContentTitle("Weather Report")
                    .setContentText("Tap Notification to see details")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // notificationId is a unique int
            notificationManager.notify(123, builder.build());
        }

    }

    private void initDb(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "AgroCrops").build();
        cropDao = db.cropDao();
    }
}