package com.ducduong.print.crf.pdfquestion;

import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;

import static com.ducduong.print.SharedConfiguration.*;

public class PdfDateTime extends PdfQuestion<Object> {
    public PdfDateTime(String name, Object obj, String defaultValue, boolean readOnly, String note, int level) {
        super(name, obj, defaultValue, readOnly, note, level);
    }

    @Override
    public float calculateHeight(PdfPrintable<String> textPrinter) {
        return Math.max(
                textPrinter.calculateHeight(name, QUESTION_WIDTH),
                FIELD_HEIGHT * 3
        );
    }

    @Override
    public void print(float x, float y, FieldPrinter fieldPrinter) {
        if (readOnly) {
            fieldPrinter.addField(name, defaultValue, x, y, true, 20);
            return;
        }
//        String[] defaultValues = defaultValue.split("T");
        fieldPrinter.addDateDropDown(name, "", x, y);
        fieldPrinter.addTimeDropDown(name, "", x, y - 20);
    }
}
