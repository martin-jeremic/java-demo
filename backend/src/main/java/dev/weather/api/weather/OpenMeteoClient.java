package dev.weather.api.weather;

import dev.weather.api.location.CityLocation;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenMeteoClient {

    private final RestClient geocodingClient;
    private final RestClient forecastClient;

    public OpenMeteoClient(
            @Qualifier("geocodingClient") RestClient geocodingClient,
            @Qualifier("forecastClient") RestClient forecastClient
    ) {
        this.geocodingClient = geocodingClient;
        this.forecastClient = forecastClient;
    }

    public List<CityLocation> searchLocations(String query) {
        try {
            GeocodingResponse response = geocodingClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/v1/search")
                            .queryParam("name", query)
                            .queryParam("count", 10)
                            .queryParam("language", "en")
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(GeocodingResponse.class);
            return response == null || response.results() == null
                    ? List.of()
                    : response.results().stream()
                            .map(result -> new CityLocation(
                                    result.id(),
                                    result.name(),
                                    result.admin1(),
                                    result.country(),
                                    result.countryCode(),
                                    result.latitude(),
                                    result.longitude()))
                            .toList();
        } catch (RestClientException exception) {
            throw new WeatherProviderException("Unable to search locations with the weather provider.", exception);
        }
    }

    public WeatherForecast getForecast(double latitude, double longitude, String locationName) {
        try {
            ForecastResponse response = forecastClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m")
                            .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                            .queryParam("forecast_days", 5)
                            .queryParam("timezone", "auto")
                            .build())
                    .retrieve()
                    .body(ForecastResponse.class);
            if (response == null || response.current() == null || response.daily() == null
                    || response.daily().time() == null
                    || response.daily().weatherCode() == null
                    || response.daily().temperatureMax() == null
                    || response.daily().temperatureMin() == null
                    || response.daily().precipitationProbabilityMax() == null) {
                throw new WeatherProviderException(
                        "The weather provider returned an incomplete forecast.");
            }

            CurrentData current = response.current();
            DailyData daily = response.daily();
            int dayCount = daily.time().size();
            if (daily.weatherCode().size() != dayCount
                    || daily.temperatureMax().size() != dayCount
                    || daily.temperatureMin().size() != dayCount
                    || daily.precipitationProbabilityMax().size() != dayCount) {
                throw new WeatherProviderException(
                        "The weather provider returned inconsistent forecast data.");
            }
            List<DailyForecast> forecast = java.util.stream.IntStream.range(0, daily.time().size())
                    .mapToObj(index -> new DailyForecast(
                            daily.time().get(index),
                            daily.temperatureMax().get(index),
                            daily.temperatureMin().get(index),
                            daily.precipitationProbabilityMax().get(index),
                            daily.weatherCode().get(index)))
                    .toList();

            return new WeatherForecast(
                    locationName,
                    response.latitude(),
                    response.longitude(),
                    response.timezone(),
                    new CurrentConditions(
                            current.time(),
                            current.temperature(),
                            current.humidity(),
                            current.feelsLike(),
                            current.dayFlag() == 1,
                            current.precipitation(),
                            current.weatherCode(),
                            current.windSpeed()),
                    forecast);
        } catch (RestClientException exception) {
            throw new WeatherProviderException("Unable to retrieve a forecast from the weather provider.", exception);
        }
    }

    private record GeocodingResponse(List<GeocodingResult> results) {
    }

    private record GeocodingResult(
            Long id,
            String name,
            String admin1,
            String country,
            @JsonProperty("country_code")
            String countryCode,
            double latitude,
            double longitude
    ) {
    }

    private record ForecastResponse(
            double latitude,
            double longitude,
            String timezone,
            CurrentData current,
            DailyData daily
    ) {
    }

    private record CurrentData(
            String time,
            @JsonProperty("temperature_2m") double temperature2m,
            @JsonProperty("relative_humidity_2m") int relativeHumidity2m,
            @JsonProperty("apparent_temperature") double apparentTemperature,
            @JsonProperty("is_day") int isDay,
            double precipitation,
            @JsonProperty("weather_code") int weatherCode,
            @JsonProperty("wind_speed_10m") double windSpeed10m
    ) {
        double temperature() {
            return temperature2m;
        }

        int humidity() {
            return relativeHumidity2m;
        }

        double feelsLike() {
            return apparentTemperature;
        }

        int dayFlag() {
            return isDay;
        }

        double windSpeed() {
            return windSpeed10m;
        }
    }

    private record DailyData(
            List<String> time,
            @JsonProperty("weather_code") List<Integer> weatherCode,
            @JsonProperty("temperature_2m_max") List<Double> temperatureMax,
            @JsonProperty("temperature_2m_min") List<Double> temperatureMin,
            @JsonProperty("precipitation_probability_max") List<Integer> precipitationProbabilityMax
    ) {
    }
}
