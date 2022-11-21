package com.ducduong.print.crf;

import org.apache.commons.lang3.StringUtils;
import com.ducduong.print.util.InformationPrinter;
import com.ducduong.print.PdfPrintUtils;
import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.crf.pdfinfo.PdfInformation;
import com.ducduong.print.crf.pdfquestion.PdfField;
import com.ducduong.print.crf.pdfquestion.PdfQuestion;

import static com.ducduong.print.pdfbox.ImagePrinter.generateBarcodeByteArray;
import static com.ducduong.print.pdfbox.ImagePrinter.generateQRCodeByteArray;

public class PdfCRFPrint extends PdfPrintUtils {

    private QuestionPrinter questionPrinter;

    private PdfCRFPrint (pdfCRFPrintBuilder builder, String pseudonym) {
        builder.withDecoration((controller) -> {
            printFooterBarCode(pseudonym);
            textPrinter.printFromRight(controller.getPageCount().toString(),
                    controller.getLowerEdge(), Float.POSITIVE_INFINITY, controller);
        });
    }

    private static final float HEADER_FONT_SIZE = 1.2f;
    private static final int HEADER_TITLE_WIDTH = 120;
    private static final int HEADER_VALUE_WIDTH = 150;

    public void printPseudonym(String pseudonym){
        if (StringUtils.isNotBlank(pseudonym)) {
            printHeaderContent("Pseudonym", pseudonym);
            return;
        }
        PdfPrintable<String> headerPrinter = textPrinter.bold().withSize(HEADER_FONT_SIZE);
        questionPrinter.withTextPrinter(headerPrinter)
                .print(new PdfField("Pseudonym:", 20, pseudonym, StringUtils.isNotBlank(pseudonym), "", 0));
    }

    public void printHeaderContent(String title, String value) {
        PdfPrintable<String> headerPrinter = textPrinter.bold().withSize(HEADER_FONT_SIZE);
        PdfPrintable<String> valuePrinter  = textPrinter.withSize(HEADER_FONT_SIZE);

        headerPrinter.print(title, HEADER_TITLE_WIDTH, controller.getLeftEdge(), controller.getCurrentHeight());
        valuePrinter.print(value, HEADER_VALUE_WIDTH, controller.getLeftEdge() + HEADER_TITLE_WIDTH + 10, controller.getCurrentHeight());
        controller.adjustCurrentHeight(valuePrinter.calculateHeight(value, HEADER_TITLE_WIDTH));
    }

    private static final float LOGO_MAX_HEIGHT = 100;

    public void printFooterBarCode(String pseudonym) {
        if (pseudonym == null || pseudonym.equals("")) return;
        float logoMaxWidth = controller.getLineWidth() - (HEADER_VALUE_WIDTH + controller.getLeftEdge() + HEADER_TITLE_WIDTH + 10);
        // print barcode
        byte[] barCode = generateBarcodeByteArray(pseudonym);
        imagePrinter.printCenter(barCode, logoMaxWidth, LOGO_MAX_HEIGHT, controller.getLowerEdge(), controller);
    }

    public void printPatientBarcode(String pseudonym) {
        float logoMaxWidth = controller.getLineWidth() - (HEADER_VALUE_WIDTH + controller.getLeftEdge() + HEADER_TITLE_WIDTH + 10);
        // print qr code
        byte[] qrCode = generateQRCodeByteArray(pseudonym);
        imagePrinter.printFromRight(qrCode, logoMaxWidth, LOGO_MAX_HEIGHT, controller.getUpperEdge(), controller);
    }

    public void printLogo(byte[] logo) {
        float logoMaxWidth = controller.getLineWidth() - (HEADER_VALUE_WIDTH + controller.getLeftEdge() + HEADER_TITLE_WIDTH + 10);
        imagePrinter.printFromRight(logo, logoMaxWidth, LOGO_MAX_HEIGHT, controller.getUpperEdge(), controller);
    }

    public void printPdfQuestion(PdfQuestion pdfQuestion) {
        questionPrinter.print(pdfQuestion);
    }

    public void printPdfInformationText(PdfInformation<String> pdfInformation) {
        InformationPrinter<String> printer = new InformationPrinter<>(controller, textPrinter);
        printer.print(pdfInformation);
    }

    public void printPdfExplainPicture(PdfInformation<byte[]> pdfInformation) {
        InformationPrinter<byte[]> printer = new InformationPrinter<>(controller, imagePrinter);
        printer.print(pdfInformation);
    }


    public static class pdfCRFPrintBuilder extends UtilsBuilder<PdfCRFPrint> {

        private String pseudonym;

        public pdfCRFPrintBuilder withPseudonym(String pseudonym) {
            this.pseudonym = pseudonym;
            return this;
        }

        @Override
        protected PdfCRFPrint createInstance() {
            return new PdfCRFPrint(this, pseudonym);
        }

        @Override
        protected void configure(PdfCRFPrint pdfCRFPrint) {
            super.configure(pdfCRFPrint);
            pdfCRFPrint.questionPrinter = new QuestionPrinter(pdfCRFPrint.textPrinter, pdfCRFPrint.controller, pdfCRFPrint.resourceProvider);
        }
    }
}
