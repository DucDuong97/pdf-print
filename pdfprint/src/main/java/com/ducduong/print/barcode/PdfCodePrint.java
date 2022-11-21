package com.ducduong.print.barcode;

import com.ducduong.print.PdfPrintUtils;

import static com.ducduong.print.pdfbox.ImagePrinter.generateBarcodeByteArray;
import static com.ducduong.print.pdfbox.ImagePrinter.generateQRCodeByteArray;

public class PdfCodePrint extends PdfPrintUtils {

    private int currentX;
    private int codePerRow;
    private int numOfCodes;
    private float codeWidth;
    private float codeHeight;

    private PdfCodePrint() {}

    public void printBarCodes(String pseudonym) {
        for (int i = 0; i < numOfCodes; i++) {
            printNextBarCode(pseudonym);
        }
    }

    public void printQRCodes(String pseudonym) {
        for (int i = 0; i < numOfCodes; i++) {
            printNextQRCode(pseudonym);
        }
    }

    private void printNextQRCode(String code) {
        byte[] codeByteArr = generateQRCodeByteArray(code);
        float blockX = controller.getLeftEdge() + currentX*codeWidth;
        float blockY = controller.getCurrentHeight();
        float qrSize = imagePrinter.calculateWidth(codeByteArr, codeWidth, codeHeight);
        float x = blockX + (codeWidth - qrSize)/2;
        float y = blockY - (codeHeight - qrSize)/2;
        // resourceProvider.drawBorder(blockX, blockY, codeWidth, codeHeight);
        imagePrinter.print(generateQRCodeByteArray(code), codeWidth, codeHeight, x, y);
        incrementCurrentCodeX();
    }

    private void printNextBarCode(String code) {
        byte[] codeByteArr = generateBarcodeByteArray(code);
        float blockX = controller.getLeftEdge() + currentX*codeWidth;
        float blockY = controller.getCurrentHeight();
        float x = blockX + (codeWidth - imagePrinter.calculateWidth(codeByteArr, codeWidth*0.8f, codeHeight*0.8f))/2;
        float y = blockY - (codeHeight - imagePrinter.calculateHeight(codeByteArr, codeWidth * 0.8f))/2;
        // resourceProvider.drawBorder(blockX, blockY, codeWidth, codeHeight);
        imagePrinter.print(codeByteArr, codeWidth * 0.8f, codeHeight * 0.8f, x, y);
        incrementCurrentCodeX();
    }

    private void incrementCurrentCodeX() {
        if (++currentX == codePerRow) {
            currentX = 0;
            controller.createNewPageIfNeeded(codeHeight);
            controller.adjustCurrentHeight(codeHeight);
        }
    }


    public static class PdfCodePrintBuilder extends UtilsBuilder<PdfCodePrint> {

        private final int DEFAULT_CODE_PER_ROW = 3;
        private final int DEFAULT_CODE_PER_COLUMN = 10;

        @Override
        protected PdfCodePrint createInstance() {
            return new PdfCodePrint();
        }

        @Override
        protected void configure(PdfCodePrint pdfCodePrint) {
            super.configure(pdfCodePrint);
            PdfBarcodeConfig barcodeConfig = (PdfBarcodeConfig) config;
            if (config != null) {
                pdfCodePrint.codeWidth = barcodeConfig.getWidth();
                pdfCodePrint.codeHeight = barcodeConfig.getHeight();
                pdfCodePrint.codePerRow = (int) (pdfCodePrint.controller.getLineWidth() / pdfCodePrint.codeWidth);
                pdfCodePrint.numOfCodes = barcodeConfig.getAmount() <= 0 ? pdfCodePrint.codePerRow*3 : barcodeConfig.getAmount();
            } else {
                pdfCodePrint.codePerRow = DEFAULT_CODE_PER_ROW;
                pdfCodePrint.numOfCodes = DEFAULT_CODE_PER_ROW * DEFAULT_CODE_PER_COLUMN;
                pdfCodePrint.codeWidth = pdfCodePrint.controller.getLineWidth() / DEFAULT_CODE_PER_ROW;
                pdfCodePrint.codeHeight = (pdfCodePrint.controller.getUpperEdge() - pdfCodePrint.controller.getLowerEdge()) / DEFAULT_CODE_PER_COLUMN;
            }
            pdfCodePrint.currentX = 0;
        }
    }
}
