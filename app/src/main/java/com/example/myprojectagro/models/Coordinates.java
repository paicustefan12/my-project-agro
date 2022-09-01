package com.example.myprojectagro.models;

public class Coordinates {
    public int Id;
    public String Latitude;
    public String Longitude;

    public Coordinates() {
        Id = 0;
        Latitude = null;
        Longitude = null;
    }

    public Coordinates(int Id, String Latitude, String Longitude) {
        this.Id = Id;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }
}
