package com.ducduong.print.crf;

import com.ducduong.print.util.Boxable;
import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;
import com.ducduong.print.crf.pdfquestion.PdfQuestion;
import com.ducduong.print.util.PositionController;
import com.ducduong.print.util.ResourceProvider;

import static com.ducduong.print.SharedConfiguration.FIELD_WIDTH;
import static com.ducduong.print.crf.pdfquestion.PdfQuestion.QUESTION_WIDTH;

public class QuestionPrinter extends Boxable<PdfQuestion> {
    private static final int QUESTION_MB = 10;
    private static final int QUESTION_MR = 20;
    private static final int FIELD_MR = 10;
    private static final int SUB_QUESTION_LEVEL_SIZE = 20;

    private final PdfPrintable<String> textPrinter;
    private final FieldPrinter fieldPrinter;
    private final PositionController controller;

    public QuestionPrinter(PdfPrintable<String> textPrinter, PositionController positionController, ResourceProvider resourceProvider) {
        this.textPrinter = textPrinter;
        this.fieldPrinter = new FieldPrinter(resourceProvider, textPrinter);
        this.controller = positionController;
        this.mb = QUESTION_MB;
    }

    public QuestionPrinter(PdfPrintable<String> textPrinter, FieldPrinter fieldPrinter, PositionController positionController) {
        this.textPrinter = textPrinter;
        this.fieldPrinter = fieldPrinter;
        this.controller = positionController;
    }

    @Override
    protected float calculateElemHeight(PdfQuestion pdfQuestion, float maxWidth) {
        return pdfQuestion.calculateHeight(textPrinter);
    }

    @Override
    protected float calculateElemWidth(PdfQuestion pdfQuestion) {
        return controller.getLineWidth();
    }

    public void print(PdfQuestion pdfQuestion) {
        float currentQuestionHeight = calculateHeight(pdfQuestion, Float.POSITIVE_INFINITY);
        controller.createNewPageIfNeeded(currentQuestionHeight);

        float questionX   = controller.getLeftEdge() + pdfQuestion.getLevel()*SUB_QUESTION_LEVEL_SIZE;
        float fieldX      = questionX + QUESTION_WIDTH + QUESTION_MR + pdfQuestion.getLevel()*SUB_QUESTION_LEVEL_SIZE;
        float constraintX = fieldX + FIELD_WIDTH + FIELD_MR + pdfQuestion.getLevel()*SUB_QUESTION_LEVEL_SIZE;
        float Y = controller.getCurrentHeight();

        controller.adjustCurrentHeight(mt);

        textPrinter.print(pdfQuestion.getName(), QUESTION_WIDTH, questionX, Y);     // print question
        pdfQuestion.print(fieldX, Y, fieldPrinter);                                 // print field
        textPrinter.print(pdfQuestion.getNote(), QUESTION_WIDTH, constraintX, Y);   // print constraint

        controller.adjustCurrentHeight(currentQuestionHeight - mt);
    }

    public QuestionPrinter withTextPrinter(PdfPrintable<String> textPrinter) {
        return new QuestionPrinter(textPrinter, fieldPrinter, controller);
    }
}
