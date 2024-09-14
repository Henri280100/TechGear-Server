package com.v01.techgear_server.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapBoxResponse {
    private String type;
    private List<String> query;
    private List<MapBoxFeature> features;
    private String attribution;
}
