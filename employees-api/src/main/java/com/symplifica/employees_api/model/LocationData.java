package com.symplifica.employees_api.model;

public class LocationData {
    private String latitude;
    private String longitude;
    private String displayName;

    public LocationData(String latitude, String longitude, String displayName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.displayName = displayName;
    }

    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getDisplayName() { return displayName; }
}