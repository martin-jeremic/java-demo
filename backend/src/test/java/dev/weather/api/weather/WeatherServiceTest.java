package dev.weather.api.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.weather.api.location.CityLocation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private OpenMeteoClient openMeteoClient;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void trimsLocationSearchBeforeCallingProvider() {
        CityLocation city = new CityLocation(1L, "London", null, "United Kingdom", "GB", 51.5, -0.1);
        when(openMeteoClient.searchLocations("London")).thenReturn(List.of(city));

        List<CityLocation> result = weatherService.searchLocations(" London ");

        assertThat(result).containsExactly(city);
        verify(openMeteoClient).searchLocations("London");
    }

    @Test
    void trimsLocationNameBeforeRequestingForecast() {
        WeatherForecast forecast = new WeatherForecast("Paris", 48.8, 2.3, "Europe/Paris", null, List.of());
        when(openMeteoClient.getForecast(48.8, 2.3, "Paris")).thenReturn(forecast);

        WeatherForecast result = weatherService.getForecast(48.8, 2.3, " Paris ");

        assertThat(result).isSameAs(forecast);
        verify(openMeteoClient).getForecast(48.8, 2.3, "Paris");
    }
}
