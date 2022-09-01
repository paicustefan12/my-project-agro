package com.example.myprojectagro.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myprojectagro.daos.CropDao;
import com.example.myprojectagro.entities.Crop;

@Database(entities = {Crop.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CropDao cropDao();
}
