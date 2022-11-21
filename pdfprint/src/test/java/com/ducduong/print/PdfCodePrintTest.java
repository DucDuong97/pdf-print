package com.ducduong.print;

import org.junit.jupiter.api.Test;
import com.ducduong.print.barcode.PdfBarcodeConfig;
import com.ducduong.print.barcode.PdfCodePrint;

import java.io.IOException;

public class PdfCodePrintTest {

    @Test
    public void printPdf() {
    }

    @Test
    public void printBarcodes() {
        try (PdfCodePrint pdfCodePrint = new PdfCodePrint.PdfCodePrintBuilder().build()) {
            pdfCodePrint.printBarCodes("pseudonym");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void printBarcodesWithConfig() {
        PdfBarcodeConfig config = new PdfBarcodeConfig();
        config.setAmount(5);
        try (PdfCodePrint pdfCodePrint = new PdfCodePrint.PdfCodePrintBuilder().withConfig(config).build()) {
            pdfCodePrint.printBarCodes("pseudonym");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void printQRCodes() {
        try (PdfCodePrint pdfCodePrint = new PdfCodePrint.PdfCodePrintBuilder().build()) {
            pdfCodePrint.printQRCodes("pseudonym");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}