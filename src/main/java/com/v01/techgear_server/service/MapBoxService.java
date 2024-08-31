package com.v01.techgear_server.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MapBoxService {
    @Value("${mapbox.access-token}")
    private String mapboxAccessToken;

    public CarmenFeature geocodeFeature(String address) {
        try {

            MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                    .accessToken(mapboxAccessToken)
                    .query(address)
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            List<CarmenFeature> results = mapboxGeocoding.executeCall().body().features();
            if (!results.isEmpty()) {
                return results.get(0);
            } else {
                log.error("No results found for the address: {}", address);
                return null;
            }
        } catch (Exception e) {
            e.getCause();
            throw new IllegalArgumentException("Failed to geocode address: " + address);
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
