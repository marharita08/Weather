package com.example.demo.model.services;

import com.example.demo.model.entities.Weather;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Component
public class FinalResponseImpl implements FinalResponse {

    private static final Logger LOGGER =
            Logger.getLogger(FinalResponseImpl.class.getName());
    private final List<WeatherService> serviceList;

    public FinalResponseImpl(List<WeatherService> serviceList) {
        this.serviceList = serviceList;
    }

    /**
     * Method reads data from external APIs and forms final response
     * @param city specified city
     * @param country country in which the specified city is located
     * @param date specified date
     * @return Weather
     * @throws Exception if wrong city name or date were specified
     */
    @Override
    public Weather getWeatherObject(String city,
                                    String country,
                                    LocalDate date) throws Exception {
        List<Weather> weatherList = new ArrayList<>(); //list of responses
        //list of exceptions
        List<Exception> exceptionList = new  ArrayList<>();
        List<Future<Weather>> list = new ArrayList<>();
        for (WeatherService tmp:serviceList) {
            //getting response from service
            list.add(tmp.readWeather(city, country, date));
        }

        for (Future<Weather> tmp:list) {
            try {
                //get Weather object and add it to weatherList
                weatherList.add(tmp.get());
            } catch (InterruptedException | ExecutionException e) {
                //add exception to the exceptionList
                LOGGER.warning(e.getMessage());
                exceptionList.add(e);
            }
        }

        if (weatherList.size() > 0) {
            LOGGER.info("Got successful response from "
                    + weatherList.size() + " external APIs");
            if (exceptionList.size() > 0) {
                LOGGER.warning("Got response with WeatherException from "
                        + exceptionList.size() + " external APIs");
            }
            Weather weather = new Weather();
            //using first response from the list
            //to initialize all params apart from temperature
            weather.setDate(weatherList.get(0).getDate());
            weather.setCity(weatherList.get(0).getCity());
            weather.setCountry(weatherList.get(0).getCountry());
            weather.setDescription(weatherList.get(0).getDescription());
            //using all responses to initialize temperature
            double sum = 0.0;
            for (Weather tmp : weatherList) {
                sum += tmp.getTemp();
            }
            double temperature = (double) Math.round(
                    sum / weatherList.size() * 10) / 10;
            weather.setTemp(temperature);
            LOGGER.info("Final response: " + weather);

            return weather;
        } else {
            LOGGER.severe("There are WeatherExceptions in all external APIs");
            throw exceptionList.get(0);
        }
    }
}
