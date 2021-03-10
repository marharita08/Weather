package com.example.demo.model.services;

import com.example.demo.model.converters.AerisWeatherConverter;
import com.example.demo.model.entities.Weather;
import com.example.demo.model.entities.WeatherException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Service
public class AerisWeather implements WeatherService {

    private static final Logger LOGGER =
            Logger.getLogger(AerisWeather.class.getName());
    private final AerisWeatherConverter converter;
    @Value("${service.key}")
    private String key;
    @Value("${aeris.host}")
    private String host;

    public AerisWeather(AerisWeatherConverter converter) {
        this.converter = converter;
    }

    /**
     * Method for getting weather data
     * for specified city using Aeris Weather API.
     * @param city city
     * @param country country in which the specified city is located
     * @param date specified date
     * @return Weather object with response data
     * @throws WeatherException if response contains error
     */
    @Override
    @Async
    public Future<Weather> readWeather(String city, String country, LocalDate date)
            throws WeatherException {
        LOGGER.info("Reading data from Aeris Weather API");
        RestTemplate restTemplate = new RestTemplate();
        //set http headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-rapidapi-key", key);
        headers.add("x-rapidapi-host", host);
        HttpEntity<?> entity = new HttpEntity<>(
                java.net.http.HttpRequest.BodyPublishers.noBody(), headers);
        //create request for Weather bit API
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://"+host+"/forecasts/"
                        + city + "," + country,
                HttpMethod.GET, entity, String.class);
        //convert got response to Weather object
        Weather weather = converter.toWeather(responseEntity.getBody(), date);
        weather.setCity(city);
        weather.setCountry(country);
        LOGGER.info("Response: " + weather);
        return new AsyncResult<>(weather);
    }
}
