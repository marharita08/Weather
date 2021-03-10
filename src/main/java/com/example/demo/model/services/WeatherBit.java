package com.example.demo.model.services;

import com.example.demo.model.converters.WeatherBitConverter;
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
public class WeatherBit implements WeatherService {
    private static final Logger LOGGER = Logger.getLogger(WeatherBit.class.getName());

    private WeatherBitConverter converter;
    @Value("${service.key}")
    private String key;
    @Value("${bit.host}")
    private String host;

    public WeatherBit(WeatherBitConverter converter) {
        this.converter = converter;
    }

    /**
     * Method for getting weather data for specified city using Weather Bit API.
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
        LOGGER.info("Reading data from Weather Bit API");
        RestTemplate restTemplate = new RestTemplate();
        //set http headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-rapidapi-key", key);
        headers.add("x-rapidapi-host", host);
        HttpEntity<?> entity = new HttpEntity<>(
                java.net.http.HttpRequest.BodyPublishers.noBody(), headers);
        //create request for Weather bit API
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://"+host+"/forecast/daily?city="
                        + city + "&country=" + country,
                HttpMethod.GET, entity, String.class);
        //convert got response to Weather object
        Weather weather = converter.toWeather(responseEntity.getBody(), date);
        LOGGER.info("Response: " + weather);
        return new AsyncResult<>(weather);
    }
}
