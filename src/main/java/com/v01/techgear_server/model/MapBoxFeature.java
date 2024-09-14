package com.v01.techgear_server.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapBoxFeature {

    private String id;
    private String type;

    @JsonProperty("place_type")
    private List<String> placeType;

    private double[] coordinates; // [longitude, latitude]

    @JsonProperty("place_name")
    private String placeName;

    // Getters and Setters
}
