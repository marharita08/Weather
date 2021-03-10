package com.example.demo.model.converters;

import com.example.demo.model.entities.Weather;
import com.example.demo.model.entities.WeatherException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.logging.Logger;

@Component
public class AerisWeatherConverter implements ResponseConverter {
    private static final Logger LOGGER =
            Logger.getLogger(AerisWeatherConverter.class.getName());

    /**
     * Method converts response from Aeris Weather API into Weather object.
     * @param response response got from external API
     * @param date specified date
     * @return Weather object with response data
     * @throws WeatherException if response contains error
     */
    @Override
    public Weather toWeather(String response, LocalDate date)
            throws WeatherException {
        LOGGER.info("Converting Aeris Weather response to Weather object");
        //converting response to JSONObject
        JSONObject jsonObject = new JSONObject(response);
        try {
            //searching amount of days between specified and current dates
            LocalDate currentDate = LocalDate.now();
            int num = (int) (date.toEpochDay() - currentDate.toEpochDay());
            //getting weather data for specified date
            JSONObject searchData = jsonObject.getJSONArray("response")
                    .getJSONObject(0).getJSONArray("periods")
                    .getJSONObject(num);

            Weather weather = new Weather();

            //read data from response
            double temp = searchData.getDouble("avgTempC");
            String description = searchData.getString("weather");
            weather.setDate(date.toString());
            weather.setTemp(temp);
            weather.setDescription(description);

            //return Weather object
            return weather;
        } catch (JSONException e) {
            //read error message from response
            String str;
            try {
                str = jsonObject.getJSONObject("error")
                        .getString("description");
            } catch (JSONException ex) {
                str = "There is no data available for this date";
            }
            LOGGER.warning(str);
            //throw WeatherException with got message
            throw new WeatherException(str);
        }
    }
}
