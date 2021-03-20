package com.example.demo.controller;

import com.example.demo.model.entities.Weather;
import com.example.demo.model.entities.WeatherException;
import com.example.demo.model.services.WeatherService;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;


@RestController
@RequestMapping(path = "/api")
public class Controller {

    private static final String XMLBASE_CURSOR_SCHEMA_PATH =
            "declare namespace w="
            + "'http://schemas.openxmlformats.org/wordprocessingml/2006/main' "
            + ".//w:fldChar/@w:fldCharType";

    private static final String XMLBASE_OBJECT_SCHEMA_PATH =
            "declare namespace w="
            + "'http://schemas.openxmlformats.org/wordprocessingml/2006/main'"
            + " .//w:ffData/w:name/@w:val";

    private static final Logger LOGGER =
            Logger.getLogger(Controller.class.getName());
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private List<WeatherService> serviceList;

    public Controller(List<WeatherService> serviceList) {
        this.serviceList = serviceList;
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
        return getWeatherObject(city, country, specifiedDate);
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
        Weather weather = getWeatherObject(city, country, specifiedDate);

        LOGGER.info("Writing response to document");

        InputStreamResource result = null;
        try {
            FileInputStream stream =
                    new FileInputStream("src/main/resources/Weather_template.docx");
            XWPFDocument docx = new XWPFDocument(stream);
            replaceFormField(docx, "dateInputField", weather.getDate());
            replaceFormField(docx, "cityInputField", weather.getCity());
            replaceFormField(docx, "countryInputField", weather.getCountry());
            replaceFormField(docx, "tempInputField",
                    String.valueOf(weather.getTemp()));
            replaceFormField(docx, "descInputField", weather.getDescription());
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            docx.write(byteArray);
            byte[] resource = byteArray.toByteArray();

            result = new InputStreamResource(
                    new ByteArrayInputStream(resource));
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
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

    private Weather getWeatherObject(String city,
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
            //to initialize all params apart of temperature
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

    private void replaceFormField(XWPFDocument document,
                                  String field, String text) {
        boolean isFound = false;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                XmlCursor cursor = run.getCTR().newCursor();
                cursor.selectPath(XMLBASE_CURSOR_SCHEMA_PATH);
                while (cursor.hasNextSelection()) {
                    cursor.toNextSelection();
                    XmlObject object = cursor.getObject();
                    if ("begin".equals(((SimpleValue) object)
                            .getStringValue())) {
                        cursor.toParent();
                        object = cursor.getObject();
                        object = object
                                .selectPath(XMLBASE_OBJECT_SCHEMA_PATH)[0];
                        isFound = field.equals(((SimpleValue) object)
                                .getStringValue());
                    } else if ("end".equals(((SimpleValue) object)
                            .getStringValue())) {
                        if (isFound) {
                            return;
                        }
                        isFound = false;
                    }
                }
                if (isFound && run.getCTR().getTList().size() > 0) {
                    run.getCTR().getTList().get(0).setStringValue(text);
                }
            }
        }
    }
}
