package dev.weather.api.weather;

public record DailyForecast(
        String date,
        double high,
        double low,
        int precipitationProbability,
        int weatherCode
) {
}
