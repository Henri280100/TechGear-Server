package com.v01.techgear_server.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.v01.techgear_server.model.MapBoxResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MapBoxService {
    @Value("${mapbox.access-token}")
    private String mapboxAccessToken;

    @Autowired
    private RestTemplate restTemplate;

    // public MapBoxService(RestTemplate restTemplate) {
    //     this.restTemplate = restTemplate;
    // }

    public MapBoxResponse geocodeFeature(String address) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.mapbox.com/geocoding/v5/mapbox.places/" + UriUtils.encode(address, StandardCharsets.UTF_8))
                .queryParam("access_token", mapboxAccessToken)
                .toUriString();

        try {
            return restTemplate.getForObject(url, MapBoxResponse.class);
        } catch (Exception e) {
            log.error("Error fetching geocoding data from MapBox: {}", e.getMessage());
            throw new RuntimeException("Failed to get geocoding data from MapBox", e);
        }
    }

    public CarmenFeature reverseGeocode(Point location) throws IOException {
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
        .accessToken(mapboxAccessToken)
            .query(Point.fromLngLat(location.longitude(), location.latitude()))
            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
            .build();

        List<CarmenFeature> results = mapboxGeocoding.executeCall().body().features();
        if (!results.isEmpty()) {
            return results.get(0);
        } else {
            log.error("No results found for the location: {}", location);
            return null;
        }
    }
}
