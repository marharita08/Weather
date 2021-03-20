package com.example.demo.model.services;

import com.example.demo.model.converters.DocumentConverter;
import com.example.demo.model.entities.Weather;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class DocumentCreatorImpl implements DocumentCreator {

    private static final Logger LOGGER =
            Logger.getLogger(DocumentCreatorImpl.class.getName());
    DocumentConverter documentConverter;

    public DocumentCreatorImpl(DocumentConverter documentConverter) {
        this.documentConverter = documentConverter;
    }

    /**
     * Method for getting document with weather data
     * @param weather object containing weather data
     * @return InputStreamResource
     */
    @Override
    public InputStreamResource getDocument(Weather weather) {
        try {
            FileInputStream stream =
                    new FileInputStream("src/main/resources/Weather_template.docx");
            XWPFDocument docx = new XWPFDocument(stream);
            documentConverter.replaceFormField(docx, "dateInputField", weather.getDate());
            documentConverter.replaceFormField(docx, "cityInputField", weather.getCity());
            documentConverter.replaceFormField(docx, "countryInputField", weather.getCountry());
            documentConverter.replaceFormField(docx, "tempInputField",
                    String.valueOf(weather.getTemp()));
            documentConverter.replaceFormField(docx, "descInputField", weather.getDescription());
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            docx.write(byteArray);
            byte[] resource = byteArray.toByteArray();

            return new InputStreamResource(
                    new ByteArrayInputStream(resource));
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            return null;
        }
    }
}
