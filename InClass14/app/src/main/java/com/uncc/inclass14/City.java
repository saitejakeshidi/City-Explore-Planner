

package com.uncc.inclass14;

public class City {
    String description, place_id;

    @Override
    public String toString() {
        return "City{" +
                "description='" + description + '\'' +
                ", place_id='" + place_id + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public String getPlace_id() {
        return place_id;
    }
}
