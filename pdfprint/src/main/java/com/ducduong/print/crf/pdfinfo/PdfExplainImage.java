package com.ducduong.print.crf.pdfinfo;

import com.ducduong.print.util.PdfPrintable;

public class PdfExplainImage extends PdfInformation<byte[]> {
    public PdfExplainImage(byte[] obj, int level) {
        super(obj, level);
    }

    @Override
    public float calculateHeight(float width, PdfPrintable<byte[]> printer) {
        return printer.calculateHeight(obj, width);
    }

    @Override
    public void print(float x, float y, float width, PdfPrintable<byte[]> printer) {
        if (obj == null || obj.length == 0) return;
        float oldMb = printer.getMb();
        printer.setMb(20);
        printer.print(obj, width, x, y);
        printer.setMb(oldMb);
    }
}
