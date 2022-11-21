package com.ducduong.print.util;

import com.ducduong.print.crf.pdfinfo.PdfInformation;

public class InformationPrinter<T> extends Boxable<PdfInformation<T>> {

    private static final int SUB_QUESTION_LEVEL_SIZE = 20;

    private final PositionController controller;
    private final PdfPrintable<T> printer;

    public InformationPrinter(PositionController controller, PdfPrintable<T> printer) {
        this.controller = controller;
        this.printer = printer;
        this.mb = 10;
    }

    @Override
    protected float calculateElemHeight(PdfInformation<T> t, float maxWidth) {
        return t.calculateHeight(maxWidth, printer);
    }

    @Override
    protected float calculateElemWidth(PdfInformation<T> tPdfInformation) {
        return controller.getLineWidth();
    }

    public void print(PdfInformation<T> info) {
        float currentQuestionHeight = calculateHeight(info, controller.getLineWidth());
        controller.adjustCurrentHeight(mt);
        controller.createNewPageIfNeeded(currentQuestionHeight);
        printer.print(info.getObj(), controller.getLineWidth(),
                controller.getLeftEdge() + info.getLevel() * SUB_QUESTION_LEVEL_SIZE, controller.getCurrentHeight());
        controller.adjustCurrentHeight(currentQuestionHeight - mt);
    }
}
