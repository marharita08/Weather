package com.example.demo.model.entities;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WeatherException extends Exception {
    public WeatherException(String message) {
        super(message);
    }
}
