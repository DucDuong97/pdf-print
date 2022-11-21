package com.ducduong.print.crf.pdfquestion;

import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;

import java.util.Arrays;
import java.util.List;

import static com.ducduong.print.SharedConfiguration.OPTION_WIDTH;

public class PdfCheckbox extends PdfQuestion<List<Button>> {
    public PdfCheckbox(String name, List<Button> obj, String defaultValue, boolean readOnly, String note, int level) {
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
        float localHeight = y;
        List<String> defaultValues = Arrays.asList(defaultValue.split(";"));
        for (var checkbox : obj) {
            boolean check = defaultValues.contains(checkbox.getName());
            localHeight -= fieldPrinter.addCheckBox(checkbox.getName(), x, localHeight, readOnly, String.valueOf(check));
        }
    }
}
