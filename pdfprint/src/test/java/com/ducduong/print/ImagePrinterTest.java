package com.ducduong.print;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.ducduong.print.pdfbox.PDFBoxResourceProvider;
import com.ducduong.print.pdfbox.ImagePrinter;
import com.ducduong.print.util.PositionController;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ImagePrinterTest {

    public static final String QR_CODE_FILE_NAME = "qrcode.png";
    public static final String BAR_CODE_FILE_NAME = "barcode.png";
    public static final String CODE = "duczuong";

    private ImagePrinter imagePrinter;
    private byte[] qrCodeByteArray;
    private byte[] barCodeByteArray;
    private PDFBoxResourceProvider doc;
    private PositionController controller;

    @BeforeEach
    public void init() {
        qrCodeByteArray = ImagePrinter.generateQRCodeByteArray("Hello World");
        barCodeByteArray = ImagePrinter.generateBarcodeByteArray("Hello World");
        doc = new PDFBoxResourceProvider();
        controller = new PositionController.ControllerBuilder(doc).build();
        imagePrinter = new ImagePrinter(doc);
        controller.createNewPage(null);
    }

    @AfterEach
    public void close() {
        try {
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCalculateWidthAndHeight() {
        assertEquals((long) imagePrinter.calculateWidth(null), 0L);
        // QR Code is a square -> width == height
        assertEquals(
                Float.valueOf(imagePrinter.calculateElemWidth(qrCodeByteArray)),
                Float.valueOf(imagePrinter.calculateElemHeight(qrCodeByteArray, Float.POSITIVE_INFINITY))
        );
        float barcodeWidth = imagePrinter.calculateElemWidth(barCodeByteArray);
        float barcodeHeight = imagePrinter.calculateElemHeight(barCodeByteArray, Float.POSITIVE_INFINITY);
        // Bar Code is a rectangle -> width > height
        assertTrue(barcodeWidth > barcodeHeight);
        float lambda = 0.5f;
        assertEquals(
                Float.valueOf(imagePrinter.calculateElemHeight(barCodeByteArray, barcodeWidth * lambda)),
                Float.valueOf(barcodeHeight * lambda)
        );
    }

    @Test
    public void testPrint() {
        float y = controller.getUpperEdge();
        // shrink with width
        imagePrinter.printElem(qrCodeByteArray, 100f, null, 0, y);
        doc.drawLineX(100f);
        doc.drawLineY(y - 100f);

        y -= 100;
        // shrink with height
        imagePrinter.printElem(qrCodeByteArray, 90f, 80f, 100f, y);
        doc.drawLineX(180f);
        doc.drawLineY(y - 80f);

        y -= 80;
        imagePrinter.printFromRight(qrCodeByteArray, y, 100f, controller);
        doc.drawLineX(controller.getRightEdge());
        doc.drawLineX(controller.getRightEdge() - 100f);
        doc.drawLineY(y - 100f);

        y -= 100;
        imagePrinter.printFromRight(qrCodeByteArray, 100f, 50f, y, controller);
        doc.drawLineX(controller.getRightEdge());
        doc.drawLineX(controller.getRightEdge() - 50f);
        doc.drawLineY(y - 50f);

        y -= 50;
        imagePrinter.printCenter(qrCodeByteArray, y, 100f, controller);
        doc.drawLineX((doc.getPageWidth() - 100) / 2);
        doc.drawLineX((doc.getPageWidth() - 100) / 2 + 100);
        doc.drawLineY(y - 100);
    }

    @Test
    public void testPrintBarCode() {
        ImagePrinter.printBarCode(CODE, BAR_CODE_FILE_NAME);
    }

    @Test
    public void testPrintQRCode() {
        ImagePrinter.printQRCode(CODE, QR_CODE_FILE_NAME);
    }
}