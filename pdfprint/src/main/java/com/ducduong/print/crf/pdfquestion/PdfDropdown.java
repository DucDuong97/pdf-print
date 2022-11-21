package com.ducduong.print.crf.pdfquestion;

import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;

import java.util.List;
import java.util.stream.Collectors;

public class PdfDropdown extends PdfQuestion<List<Button>> {

    public PdfDropdown(String name, List<Button> obj, String defaultValue, boolean readOnly, String note, int level) {
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
        List<String> values = obj.stream().map(Button::getName).collect(Collectors.toList());
        fieldPrinter.addDropdownMenu(name, defaultValue, x, y, readOnly, values);

    }
}
