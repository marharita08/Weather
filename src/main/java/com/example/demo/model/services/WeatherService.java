package com.example.demo.model.services;

import com.example.demo.model.entities.Weather;
import com.example.demo.model.entities.WeatherException;

import java.time.LocalDate;
import java.util.concurrent.Future;

public interface WeatherService {

    /**
     * Method for getting weather data for specified city using external API.
     * @param city city
     * @param country country in which the specified city is located
     * @param date specified date
     * @return Weather object with response data
     * @throws WeatherException if response contains error
     */
    Future<Weather> readWeather(String city, String country, LocalDate date) throws WeatherException;
}
