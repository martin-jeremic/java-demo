package dev.weather.api.weather;

import dev.weather.api.location.CityLocation;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final OpenMeteoClient openMeteoClient;

    public WeatherService(OpenMeteoClient openMeteoClient) {
        this.openMeteoClient = openMeteoClient;
    }

    public List<CityLocation> searchLocations(String query) {
        return openMeteoClient.searchLocations(query.trim());
    }

    public WeatherForecast getForecast(double latitude, double longitude, String locationName) {
        return openMeteoClient.getForecast(latitude, longitude, locationName.trim());
    }
}
