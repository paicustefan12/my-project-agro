package com.example.myprojectagro.services;

import com.example.myprojectagro.models.Coordinates;
import com.example.myprojectagro.models.WeatherResponseCultureModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WeatherService {
    @POST("api/weather")
    Call<ArrayList<WeatherResponseCultureModel>> getWeather(@Body ArrayList<Coordinates> coordinates);

}
