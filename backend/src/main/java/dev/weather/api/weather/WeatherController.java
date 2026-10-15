package dev.weather.api.weather;

import dev.weather.api.location.CityLocation;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/locations")
    public List<CityLocation> searchLocations(
            @RequestParam @NotBlank @Size(max = 100) String query
    ) {
        return weatherService.searchLocations(query);
    }

    @GetMapping("/forecast")
    public WeatherForecast getForecast(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
            @RequestParam @NotBlank @Size(max = 100) String name
    ) {
        return weatherService.getForecast(latitude, longitude, name);
    }
}
