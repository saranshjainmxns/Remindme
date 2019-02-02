package com.example.remindme;

public class PlaceDataModel {
    String id;
    String name;
    String category;
    String imageUrl;
    double lat;
    double lng;


    public PlaceDataModel(String id, String name, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public PlaceDataModel withLatLong(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "PlaceDataModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
