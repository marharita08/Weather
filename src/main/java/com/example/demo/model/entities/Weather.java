package com.example.demo.model.entities;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class Weather {

    private String date;
    private String city;
    private String country;
    private double temp;
    private String description;


    public Weather(String date, String city, String country, double temp, String description) {
        this.date = date;
        this.city = city;
        this.country = country;
        this.temp = temp;
        this.description = description;
    }

    public Weather() {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "date='" + date + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", temp=" + temp +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather weather = (Weather) o;
        return Double.compare(weather.temp, temp) == 0
                && Objects.equals(date, weather.date)
                && Objects.equals(city, weather.city)
                && Objects.equals(country, weather.country)
                && Objects.equals(description, weather.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, city, country, temp, description);
    }
}
