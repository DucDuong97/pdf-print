package com.ducduong.print.crf.pdfquestion;

import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;

import java.util.List;

import static com.ducduong.print.SharedConfiguration.*;

public class PdfRadioButton extends PdfQuestion<List<Button>> {
    public PdfRadioButton(String name, List<Button> obj, String defaultValue, boolean readOnly, String note, int level) {
        super(name, obj, defaultValue, readOnly, note, level);
    }

    @Override
    public float calculateHeight(PdfPrintable<String> textPrinter) {
        int buttonHeight = 0;
        for (var button : obj) {
            buttonHeight += textPrinter.calculateHeight(button.getName(), OPTION_WIDTH);
        }
        return Math.max(
                textPrinter.calculateHeight(name, QUESTION_WIDTH),
                buttonHeight
        );
    }

    @Override
    public void print(float x, float y, FieldPrinter fieldPrinter) {
        fieldPrinter.addRadioButtons(name, defaultValue, x, y, readOnly, obj);
    }
}
