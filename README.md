# Weather API

The Weather API is a web-based application that provides weather forecasts by retrieving data from three external APIs. It offers an aggregated result based on the available APIs and ensures that the forecast is still provided even if one or more of the external APIs are not functioning. The API returns the forecast in either JSON or XML format.

To access the weather forecast, simply open the following URL in your browser: `apiurl:port/api/weather?parameters`. The API supports the following parameters:

- `city` (required): The name of the city for which you want to retrieve the weather forecast.
- `country` (required): The name of the country associated with the city.
- `date` (optional): The date in the format `yyyy-MM-dd`. If not provided, the current date is used as the default.
- `mediaType` (optional): The type of response desired, either `xml` or `json`. If not specified, the default response type is JSON.

Example usage: `localhost:9090/api/weather?city=Sumy&country=UA&date=2023-07-10&mediaType=json`

Even if some of the external APIs are not functioning, the application will still provide a forecast based on the available data. This ensures that users can receive weather information regardless of the status of individual APIs.

Note: Currently only one of three used external APIs is available

## Downloading the Forecast

Additionally, you have the option to download the forecast in a Word document format. To do so, open the following URL in your browser: `apiurl:port/api/download?parameters`, using the same parameters as described above.

Example usage: `localhost:9090/api/download?city=Sumy&country=UA`

## Response Format

The API response will be in either JSON or XML format, depending on the specified `mediaType` parameter. The response will include the relevant weather forecast data for the requested city and date.

Example JSON response:
```json
{
  "city": "Sumy",
  "country": "UA",
  "date": "2023-07-10",
  "temp": 18.8,
  "description": "Broken clouds"
}
```

```xml
<weather>
    <city>Sumy</city>
    <country>UA</country>
    <date>2023-07-10</date>
    <temp>18.8</temp>
    <description>Broken clouds</description>
</weather>
```

