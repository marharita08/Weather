package com.example.demo.model.services;

import com.example.demo.model.entities.Weather;
import org.springframework.core.io.InputStreamResource;

public interface DocumentCreator {
    /**
     * Method for getting document with weather data
     * @param weather object containing weather data
     * @return InputStreamResource
     */
    InputStreamResource getDocument(Weather weather);
}
