package com.example.myprojectagro.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Crop {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "field_size")
    public Double fieldSize;

    @ColumnInfo(name = "field_latitude")
    public String fieldLatitude;

    @ColumnInfo(name = "field_longitude")
    public String fieldLongitude;

    @ColumnInfo(name = "daily_weather")
    public String dailyWeather;

    public Crop(String type, Double fieldSize, String fieldLatitude, String fieldLongitude) {
        this.type = type;
        this.fieldSize = fieldSize;
        this.fieldLatitude = fieldLatitude;
        this.fieldLongitude = fieldLongitude;
    }
}
