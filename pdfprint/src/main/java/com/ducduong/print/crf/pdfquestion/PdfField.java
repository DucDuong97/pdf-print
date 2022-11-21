package com.ducduong.print.crf.pdfquestion;

import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;

import static com.ducduong.print.SharedConfiguration.*;

public class PdfField extends PdfQuestion<Integer> {

    public PdfField(String name, Integer obj, String defaultValue, boolean readOnly, String note, int level) {
        super(name, obj, defaultValue, readOnly, note, level);
    }

    @Override
    public float calculateHeight(PdfPrintable<String> textPrinter) {
        int numOfLines = 1;
        if (obj != null && obj > LETTERS_PER_LINE) {
            numOfLines = (obj % LETTERS_PER_LINE == 0 ? 0 : 1) +
                    obj / LETTERS_PER_LINE;
        }
        return Math.max(
                textPrinter.calculateHeight(name, QUESTION_WIDTH),
                FIELD_HEIGHT * numOfLines
        );
    }

    @Override
    public void print(float x, float y, FieldPrinter fieldPrinter) {
        fieldPrinter.addField(name, defaultValue, x, y, readOnly, obj);
    }
}
