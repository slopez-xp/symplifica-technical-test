package com.symplifica.employees_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symplifica.employees_api.model.LocationData;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenStreetMapService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final java.util.Map<String, LocationData> cache = new java.util.concurrent.ConcurrentHashMap<>();

    public LocationData getLocation(String city) {
        if (cache.containsKey(city)) {
            return cache.get(city);
        }
        try {
            // add a small delay to respect rate limits
            Thread.sleep(1000);

            String encodedCity = java.net.URLEncoder.encode(city, java.nio.charset.StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q="
                    + encodedCity + "&format=json&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "symplifica-app")
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            String body = response.body().trim();
            if (!body.startsWith("[") && !body.startsWith("{")) {
                System.out.println("Nominatim returned non-JSON for city: " + city);
                return null;
            }

            JsonNode root = objectMapper.readTree(body);

            if (root.isArray() && !root.isEmpty()) {
                JsonNode first = root.get(0);
                LocationData location = new LocationData(
                        first.get("lat").asText(),
                        first.get("lon").asText(),
                        first.get("display_name").asText()
                );
                cache.put(city, location);
                return location;
            }

        } catch (Exception e) {
            System.out.println("OpenStreetMap error for city: " + city + " — " + e.getMessage());
        }
        return null;
    }
}