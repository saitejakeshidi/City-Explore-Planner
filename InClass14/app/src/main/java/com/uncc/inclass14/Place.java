package com.uncc.inclass14;

import java.io.Serializable;

public class Place implements Serializable {

    String name, lat, lng, imageUrl, id, tripID;

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public Place(String name, String lat, String lng, String imageUrl, String id, String tripID) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.imageUrl = imageUrl;
        this.id = id;
        this.tripID = tripID;
    }

    public String getName() {
        return name;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
