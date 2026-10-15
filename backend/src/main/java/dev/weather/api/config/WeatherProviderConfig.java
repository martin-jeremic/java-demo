package dev.weather.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WeatherProviderConfig {

    @Bean
    @Qualifier("geocodingClient")
    RestClient geocodingClient(RestClient.Builder builder) {
        return builder.baseUrl("https://geocoding-api.open-meteo.com").build();
    }

    @Bean
    @Qualifier("forecastClient")
    RestClient forecastClient(RestClient.Builder builder) {
        return builder.baseUrl("https://api.open-meteo.com").build();
    }
}
