package dev.weather.api.weather;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.weather.api.location.CityLocation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Test
    void returnsCitySearchResults() throws Exception {
        when(weatherService.searchLocations("Copenhagen"))
                .thenReturn(List.of(new CityLocation(1L, "Copenhagen", "Capital Region", "Denmark", "DK", 55.68, 12.57)));

        mockMvc.perform(get("/api/weather/locations").param("query", "Copenhagen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Copenhagen"))
                .andExpect(jsonPath("$[0].countryCode").value("DK"));
    }

    @Test
    void rejectsCoordinatesOutsideValidRanges() throws Exception {
        mockMvc.perform(get("/api/weather/forecast")
                        .param("latitude", "91")
                        .param("longitude", "12")
                        .param("name", "Copenhagen"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void allowsTheLocalViteOrigin() throws Exception {
        mockMvc.perform(options("/api/weather/locations")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
