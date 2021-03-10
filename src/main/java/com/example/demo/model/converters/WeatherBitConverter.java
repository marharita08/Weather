package com.example.demo.model.converters;

import com.example.demo.model.entities.Weather;
import com.example.demo.model.entities.WeatherException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.logging.Logger;

@Component
public class WeatherBitConverter implements ResponseConverter {
    private static final Logger LOGGER =
            Logger.getLogger(WeatherBitConverter.class.getName());

    /**
     * Method converts response from Weather Bit API into Weather object.
     * @param response response got from external API
     * @param date specified date
     * @return Weather object with response data
     * @throws WeatherException if response contains error
     */
    @Override
    public Weather toWeather(String response, LocalDate date)
            throws WeatherException {
        LOGGER.info("Converting Weather Bit response to Weather object");
        try {
            //converting response to JSONObject
            JSONObject jsonObject = new JSONObject(response);
            //searching amount of days between specified and current dates
            LocalDate currentDate = LocalDate.now();
            int num = (int) (date.toEpochDay() - currentDate.toEpochDay());
            //getting weather data for specified date
            JSONObject searchData = jsonObject
                    .getJSONArray("data").getJSONObject(num);

            //read data from response
            String city = jsonObject.getString("city_name");
            String country = jsonObject.getString("country_code");
            double temp = searchData.getDouble("temp");
            String description = searchData
                    .getJSONObject("weather").getString("description");

            //return Weather object
            return new Weather(date.toString(),
                    city, country, temp, description);

        } catch (NullPointerException e) {
            //if request was wrong Weather bit API doesn't return any response
            String str = "There is no response from WeatherBit";
            LOGGER.warning(str);
            throw new WeatherException(str);
        } catch (JSONException e) {
            String str = "There is no data available for this date";
            LOGGER.warning(str);
            //throw WeatherException with got message
            throw new WeatherException(str);
        }
    }
}
