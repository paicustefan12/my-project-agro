package com.example.myprojectagro.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myprojectagro.entities.Crop;

import java.util.List;

@Dao
public interface CropDao {
    @Query("SELECT * FROM Crop")
    List<Crop> getAll();

    @Query("SELECT * FROM crop WHERE id IN (:cropIds)")
    List<Crop> loadAllByIds(int[] cropIds);

    @Insert
    void insertAll(Crop... crops);

    @Delete
    void delete(Crop crop);

    @Query("UPDATE crop SET daily_weather = :weather")
    void updateAll(String weather);

    @Query("UPDATE crop SET daily_weather = :weather WHERE id = :id")
    void updateById(String weather, int id);

    @Query("SELECT * FROM crop LIMIT :limit")
    List<Crop> findLimit(int limit);
}
