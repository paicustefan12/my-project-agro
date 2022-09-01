package com.example.myprojectagro.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherResponseCultureModel {
    @SerializedName("id")
    public int Id;

    @SerializedName("condition")
    public ArrayList<String> Condition;

    public WeatherResponseCultureModel() {
        Id = 0;
        Condition = new ArrayList<>();
    }

    public WeatherResponseCultureModel(int id, ArrayList<String> condition) {
        this.Id = id;
        this.Condition = condition;
    }
}
