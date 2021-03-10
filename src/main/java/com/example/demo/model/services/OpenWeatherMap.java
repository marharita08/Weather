package com.example.demo.model.services;

import com.example.demo.model.converters.OpenWeatherMapConverter;
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
public class OpenWeatherMap implements WeatherService {
    private static final Logger LOGGER =
            Logger.getLogger(OpenWeatherMap.class.getName());
    private OpenWeatherMapConverter converter;
    @Value("${service.key}")
    private String key;
    @Value("${openmap.host}")
    private String host;

    public OpenWeatherMap(OpenWeatherMapConverter converter) {
        this.converter = converter;
    }

    /**
     * Method for getting weather data
     * for specified city using Open Weather Map API.
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
        LOGGER.info("Reading data from Open Weather Map API");
        try {
            RestTemplate restTemplate = new RestTemplate();
            //set http headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("x-rapidapi-key", key);
            headers.add("x-rapidapi-host", host);
            HttpEntity<?> entity = new HttpEntity<>(
                    java.net.http.HttpRequest.BodyPublishers.noBody(), headers);
            //create request for Weather bit API
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "https://"+host+"/forecast?q="
                            + city + "," + country,
                    HttpMethod.GET, entity, String.class);
            //convert got response to Weather object
            Weather weather = converter.toWeather(responseEntity.getBody(), date);
            LOGGER.info("Response: " + weather);
            return new AsyncResult<>(weather);
        } catch (Exception e) {
            //convert got exception message to WeatherException
            if (!(e instanceof WeatherException)) {
                return new AsyncResult<>(converter.toWeather(e.getMessage(), date));
            } else {
                throw e;
            }
        }
    }
}
