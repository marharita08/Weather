package com.example.demo.model.converters;

import com.example.demo.model.entities.Weather;
import com.example.demo.model.entities.WeatherException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.logging.Logger;

@Component
public class OpenWeatherMapConverter implements ResponseConverter {
    private static final Logger LOGGER =
            Logger.getLogger(OpenWeatherMapConverter.class.getName());

    /**
     * Method converts response from Open Weather Map API into Weather object.
     * @param response response got from external API
     * @param date specified date
     * @return Weather object with response data
     * @throws WeatherException if response contains error
     */
    @Override
    public Weather toWeather(String response, LocalDate date)
            throws WeatherException {
        LOGGER.info("Converting Open Weather Map response to Weather object");
        try {
            //converting response to JSONObject
            JSONObject jsonObject = new JSONObject(response);
            //searching amount of days between specified and current dates
            LocalDate currentDate = LocalDate.now();
            int num = (int) (date.toEpochDay() - currentDate.toEpochDay());
            //getting weather data for specified date
            JSONObject searchData = jsonObject
                    .getJSONArray("list").getJSONObject(num * 8);

            //read data from response
            String city = jsonObject.getJSONObject("city").getString("name");
            String country = jsonObject
                    .getJSONObject("city").getString("country");

            double temp = (double) (Math.round((
                    searchData.getJSONObject("main")
                    .getDouble("temp") - 273.15) * 10)) / 10;

            String description = searchData.getJSONArray("weather")
                    .getJSONObject(0).getString("description");

            //return Weather object
            return new Weather(date.toString(), city,
                    country, temp, description);

        } catch (JSONException e) {
            String str;
            try {
                //convert error response to JSONObject
                response = response.substring(16, response.length() - 1);
                JSONObject jsonObject = new JSONObject(response);
                //read error message from response
                str = jsonObject.getString("message");
            } catch (JSONException ex) {
                str = "There is no data available for this date";
            }
            LOGGER.warning(str);
            //throw WeatherException with got message
            throw new WeatherException(str);
        }
    }
}
