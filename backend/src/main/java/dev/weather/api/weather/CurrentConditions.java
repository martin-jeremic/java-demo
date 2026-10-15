package dev.weather.api.weather;

public record CurrentConditions(
        String time,
        double temperature,
        int humidity,
        double feelsLike,
        boolean daytime,
        double precipitation,
        int weatherCode,
        double windSpeed
) {
}
