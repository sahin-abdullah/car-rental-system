package com.rental.car.common;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String API_BASE = "https://nominatim.openstreetmap.org/search";

    @Cacheable(value = "geocoding", key = "#fullAddress.toLowerCase().trim()")
    public double[] getCoordinates(String fullAddress) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", "CarRentalDemo/1.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Properly build URI with encoded query parameters
            URI uri = UriComponentsBuilder.fromUriString(API_BASE)
                    .queryParam("q", fullAddress)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .build()
                    .toUri();

            System.out.println("Geocoding: " + fullAddress + " -> " + uri);

            ResponseEntity<List<Map<String, Object>>> responseEntity = restTemplate.exchange(
                uri, 
                HttpMethod.GET, 
                entity, 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            List<Map<String, Object>> response = responseEntity.getBody();

            if (response != null && !response.isEmpty()) {
                Map<String, Object> location = response.get(0);
                double lat = Double.parseDouble(location.get("lat").toString());
                double lon = Double.parseDouble(location.get("lon").toString());
                System.out.println("Geocoded successfully: [" + lat + ", " + lon + "]");
                return new double[]{lat, lon};
            } else {
                System.err.println("Geocoding returned no results for: " + fullAddress);
            }
        } catch (Exception e) {
            System.err.println("Geocoding failed for '" + fullAddress + "': " + e.getMessage());
            e.printStackTrace();
        }
        return new double[]{0.0, 0.0};
    }
}
