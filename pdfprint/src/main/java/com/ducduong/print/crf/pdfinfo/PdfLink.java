package com.ducduong.print.crf.pdfinfo;

import com.ducduong.print.util.PdfPrintable;

public class PdfLink extends PdfInformation<String> {
    public PdfLink(String obj, int level) {
        super(obj, level);
    }

    @Override
    public float calculateHeight(float width, PdfPrintable<String> printer) {
        return printer.calculateHeight(obj, width);
    }

    @Override
    public void print(float x, float y, float width, PdfPrintable<String> printer) {
        printer.print(obj, width, x, y);
        //TODO implement link
    }
}
