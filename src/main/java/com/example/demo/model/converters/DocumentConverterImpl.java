package com.example.demo.model.converters;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.springframework.stereotype.Component;

@Component
public class DocumentConverterImpl implements DocumentConverter {

    private static final String XMLBASE_CURSOR_SCHEMA_PATH =
            "declare namespace w="
                    + "'http://schemas.openxmlformats.org/wordprocessingml/2006/main' "
                    + ".//w:fldChar/@w:fldCharType";

    private static final String XMLBASE_OBJECT_SCHEMA_PATH =
            "declare namespace w="
                    + "'http://schemas.openxmlformats.org/wordprocessingml/2006/main'"
                    + " .//w:ffData/w:name/@w:val";

    /**
     * Method replaces form field content in the document for specified text.
     * @param document specified document
     * @param field field from specified document
     * @param text specified text
     */
    @Override
    public void replaceFormField(XWPFDocument document, String field, String text) {
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
