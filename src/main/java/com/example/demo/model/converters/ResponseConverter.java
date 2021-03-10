package com.example.demo.model.converters;

import com.example.demo.model.entities.Weather;
import com.example.demo.model.entities.WeatherException;

import java.time.LocalDate;

public interface ResponseConverter {

    /**
     * Method convert response into Weather object
     * @param response response got from external API
     * @param date required date
     * @return Weather object with response data
     * @throws WeatherException if response contains error
     */
    Weather toWeather(String response, LocalDate date) throws WeatherException;
}
