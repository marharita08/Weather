package com.example.demo.model.converters;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public interface DocumentConverter {
    /**
     * Method replaces form field content in the document for specified text.
     * @param document specified document
     * @param field field from specified document
     * @param text specified text
     */
    void replaceFormField(XWPFDocument document, String field, String text);
}
