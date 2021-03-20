package com.example.demo.controller;

import com.example.demo.model.entities.Weather;
import com.example.demo.model.entities.WeatherException;
import com.example.demo.model.services.DocumentCreator;
import com.example.demo.model.services.FinalResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;


@RestController
@RequestMapping(path = "/api")
public class Controller {

    private static final Logger LOGGER =
            Logger.getLogger(Controller.class.getName());
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private FinalResponse finalResponse;
    private DocumentCreator documentCreator;

    public Controller(FinalResponse finalResponse, DocumentCreator documentCreator) {
        this.finalResponse = finalResponse;
        this.documentCreator = documentCreator;
    }

    /**
     * Method for getting weather data for specified city.
     * @param city city
     * @param country country in which the specified city is located
     * @param date specified date
     * @return Weather
     * @throws WeatherException if wrong city name or date were specified
     */
    @RequestMapping(value = "/weather",
            produces = { "application/json", "application/xml" },
            method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Weather getWeather(@RequestParam String city,
                              @RequestParam String country,
                              @RequestParam(required = false) String date)
            throws Exception {
        LocalDate specifiedDate;
        if (date == null) {
            LOGGER.info("Request: get current weather data for "
                    + city + ", " + country);
            specifiedDate = LocalDate.now();
        } else {
            LOGGER.info("Request: get weather data for "
                    + city + ", " + country + " for " + date);
            try {
                specifiedDate = LocalDate.parse(date, FORMATTER);
            } catch (DateTimeParseException e) {
                throw new WeatherException("Date has wrong format."
                        + " Input date in format 'yyyy-MM-dd'");
            }
        }
        return finalResponse.getWeatherObject(city, country, specifiedDate);
    }

    /**
     * Method for downloading weather data for specified city.
     * @param city city
     * @param country country in which the specified city is located
     * @param date specified date
     * @return ResponseEntity<?>
     * @throws WeatherException if wrong city name or date were specified
     */
    @RequestMapping(path = "download", method = RequestMethod.GET)
    public ResponseEntity<?> download(@RequestParam String city,
                                      @RequestParam String country,
                                      @RequestParam(required = false) String date)
            throws Exception {
        LocalDate specifiedDate;
        if (date == null) {
            LOGGER.info("Request: download current weather data for "
                    + city + ", " + country);
            specifiedDate = LocalDate.now();
        } else {
            LOGGER.info("Request: download weather data for "
                    + city + ", " + country + " for " + date);
            try {
                specifiedDate = LocalDate.parse(date, FORMATTER);
            } catch (DateTimeParseException e) {
                LOGGER.severe("Date has wrong format");
                throw new WeatherException("Date has wrong format."
                        + " Input date in format 'yyyy-MM-dd'");
            }
        }

        Weather weather = finalResponse.getWeatherObject(city,
                country, specifiedDate);
        LOGGER.info("Writing response to document");

        InputStreamResource result = documentCreator.getDocument(weather);

        if (result != null) {
            String headerValue = String.format(
                    "attachment;filename=Weather_%s_%s_%s.docx",
                    city, country, specifiedDate.toString());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(result);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
