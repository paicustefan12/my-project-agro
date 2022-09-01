package com.example.myprojectagro;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myprojectagro.daos.CropDao;
import com.example.myprojectagro.databases.AppDatabase;
import com.example.myprojectagro.entities.Crop;

import java.util.ArrayList;

public class WeatherActivity extends BaseActivity {
    private final String TAG = "weather_activity";

    private LinearLayout linearLayout;
    private AppDatabase db;
    private CropDao cropDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initUi();
        initDb();

        getCrops();
    }

    private void initUi() {
        linearLayout = findViewById(R.id.weather_layout);
    }

    private void initDb() {
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AgroCrops").build();
        cropDao = db.cropDao();
    }

    private void getCrops() {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                ArrayList<Crop> crops = (ArrayList<Crop>) inputMessage.obj;
                Log.d(TAG, crops.toString());
                // TODO: GET DATA AND POST IT
                displayCrops(crops);
            }
        };

        Runnable runnable = () -> {
            ArrayList<Crop> crops = new ArrayList<>(cropDao.getAll());

            Message message = handler.obtainMessage();
            message.obj = crops;
            message.sendToTarget();
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void displayCrops(ArrayList<Crop> crops) {
        int i = 0;
        for (Crop crop:crops) {
            displayCrop(crop, i++);
        }
    }

    private void displayCrop(Crop crop, int counter) {
        CardView cardView = new CardView(this);
        cardView.setId(counter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        params.gravity = Gravity.CENTER;
        cardView.setLayoutParams(params);
        if (counter % 2 == 0) {
            cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey));
        }
        LinearLayout layoutCardParent = new LinearLayout(this);
        layoutCardParent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutCardParent.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout layoutCard = new LinearLayout(this);
        layoutCard.setLayoutParams(new LinearLayout.LayoutParams(750, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutCard.setOrientation(LinearLayout.VERTICAL);

        TextView textView1 = new TextView(this);
        textView1.setText("Type: " + crop.type);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView1.setLayoutParams(params2);

        TextView textView2 = new TextView(this);
        textView2.setText("Location: " + crop.fieldLatitude + " lat   " + crop.fieldLongitude + " long");
        textView2.setLayoutParams(params2);

        TextView textView3 = new TextView(this);
        textView3.setText("Field size: " + crop.fieldSize);
        textView3.setLayoutParams(params2);

        TextView textView4 = new TextView(this);
        textView3.setText("Weather: " + crop.dailyWeather);
        textView3.setLayoutParams(params2);

        ImageButton deleteBtn = new ImageButton(this);
        deleteBtn.setImageResource(R.drawable.delete);
        deleteBtn.setBackground(null);
        LinearLayout.LayoutParams deleteBtnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteBtnParams.gravity = Gravity.CENTER;
        deleteBtn.setLayoutParams(deleteBtnParams);
        deleteBtn.setOnClickListener(v -> {
            Runnable runnable = () -> {
                cropDao.delete(crop);
            };

            Thread thread = new Thread(runnable);
            thread.start();
        });

        layoutCard.addView(textView1);
        layoutCard.addView(textView2);
        layoutCard.addView(textView3);
        layoutCard.addView(textView4);

        layoutCardParent.addView(layoutCard);
        layoutCardParent.addView(deleteBtn);

        cardView.addView(layoutCardParent);

        linearLayout.addView(cardView);
    }
}