package com.example.demo.model.services;

import com.example.demo.model.entities.Weather;

import java.time.LocalDate;

public interface FinalResponse {
    /**
     * Method reads data from external APIs and forms final response
     * @param city specified city
     * @param country country in which the specified city is located
     * @param date specified date
     * @return Weather
     * @throws Exception if wrong city name or date were specified
     */
    Weather getWeatherObject(String city,
                             String country,
                             LocalDate date) throws Exception;
}
