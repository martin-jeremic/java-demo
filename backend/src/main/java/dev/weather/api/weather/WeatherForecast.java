package dev.weather.api.weather;

import java.util.List;

public record WeatherForecast(
        String location,
        double latitude,
        double longitude,
        String timezone,
        CurrentConditions current,
        List<DailyForecast> daily
) {
}
