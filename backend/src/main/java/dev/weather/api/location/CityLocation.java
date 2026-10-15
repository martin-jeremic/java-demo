package dev.weather.api.location;

public record CityLocation(
        Long id,
        String name,
        String region,
        String country,
        String countryCode,
        double latitude,
        double longitude
) {
}
