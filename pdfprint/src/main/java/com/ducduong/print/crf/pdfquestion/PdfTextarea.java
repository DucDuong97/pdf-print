package com.ducduong.print.crf.pdfquestion;

import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;

import static com.ducduong.print.SharedConfiguration.*;

public class PdfTextarea extends PdfQuestion<Integer> {
    public PdfTextarea(String name, Integer obj, String defaultValue, boolean readOnly, String note, int level) {
        super(name, obj, defaultValue, readOnly, note, level);
    }

    @Override
    public float calculateHeight(PdfPrintable<String> textPrinter) {
        return Math.max(
                textPrinter.calculateHeight(name, QUESTION_WIDTH),
                FIELD_HEIGHT * obj
        );
    }

    @Override
    public void print(float x, float y, FieldPrinter fieldPrinter) {
        int answerLen = obj * LETTERS_PER_LINE;
        fieldPrinter.addField(name, defaultValue, x, y, readOnly, answerLen);
    }
}
