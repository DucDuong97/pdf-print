package com.ducduong.print.crf.pdfquestion;

import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;

public class PdfDate extends PdfQuestion<Object> {
    public PdfDate(String name, Object obj, String defaultValue, boolean readOnly, String note, int level) {
        super(name, obj, defaultValue, readOnly, note, level);
    }

    @Override
    public float calculateHeight(PdfPrintable<String> textPrinter) {
        return textPrinter.calculateHeight(name, QUESTION_WIDTH);
    }

    @Override
    public void print(float x, float y, FieldPrinter fieldPrinter) {
        if (readOnly) {
            fieldPrinter.addField(name, defaultValue, x, y, true, 20);
            return;
        }
        fieldPrinter.addDateDropDown(name, defaultValue, x, y);
    }
}
