package com.example.remindme.mapper;

import com.example.remindme.PlaceDataModel;
import com.google.maps.model.PlacesSearchResult;

public class PlaceMapper {

    public static PlaceDataModel map(String category, PlacesSearchResult result) {
        PlaceDataModel placeDataModel = new PlaceDataModel(result.placeId, result.name, category, result.icon.toString());
        placeDataModel.withLatLong(result.geometry.location.lat, result.geometry.location.lng);
        return placeDataModel;
    }
}
