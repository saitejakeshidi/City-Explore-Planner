package com.uncc.inclass14;

import java.util.List;

public class Trip {
    String tripName, cityName, placeId, lat, lng;
    List<Place> placeList;

    public Trip(String tripName, String cityName, String placeId, String lat, String lng, List<Place> placeList) {
        this.tripName = tripName;
        this.cityName = cityName;
        this.placeId = placeId;
        this.lat = lat;
        this.lng = lng;
        this.placeList = placeList;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripName='" + tripName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", placeId='" + placeId + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", placeList=" + placeList +
                '}';
    }

    public String getTripName() {
        return tripName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public List<Place> getPlaceList() {
        return placeList;
    }
}
