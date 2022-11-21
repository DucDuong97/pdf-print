package com.ducduong.print;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.ducduong.print.pdfbox.PDFBoxResourceProvider;
import com.ducduong.print.util.PositionController;
import com.ducduong.print.pdfbox.TextPrinter;

import java.io.IOException;

public class TextPrinterTest {

    private static final String SAMPLE = "Hello World";
    private static final String SAMPLE_2 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ";

    private TextPrinter textPrinter;
    private PDFBoxResourceProvider doc;
    private PositionController controller;

    @BeforeEach
    public void init() {
        doc = new PDFBoxResourceProvider();
        textPrinter = new TextPrinter(doc);
        controller = new PositionController.ControllerBuilder(doc).build();
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
    public void testPrint() {
        float y = doc.getPageWidth();

        // printFromRight and Center on its width
        float sampleWidth = textPrinter.calculateWidth(SAMPLE);
        float sampleHeight = textPrinter.calculateHeight(SAMPLE, Float.POSITIVE_INFINITY);
        System.out.println(sampleHeight);
        float height = controller.getUpperEdge();
        doc.drawLineY(height);
        textPrinter.printFromRight(SAMPLE, height, Float.POSITIVE_INFINITY, controller);
        doc.drawLineX(controller.getRightEdge());
        doc.drawLineX(controller.getRightEdge() - sampleWidth);

        height -= sampleHeight;
        doc.drawLineY(height);
        textPrinter.printCenter(SAMPLE, height, Float.POSITIVE_INFINITY, controller);
        doc.drawLineX((y - sampleWidth) / 2);
        doc.drawLineX((y - sampleWidth) / 2 + sampleWidth);

        height -= sampleHeight;
        // printFromRight and Center on fixed width
        float sample2Width = 120f;
        float sample2Height = textPrinter.calculateHeight(SAMPLE_2, sample2Width);
        doc.drawLineY(height);
        textPrinter.printFromRight(SAMPLE_2, height, sample2Width, controller);
        doc.drawLineX(controller.getRightEdge() - sample2Width);

        height -= sample2Height;
        doc.drawLineY(height);
        textPrinter.printCenter(SAMPLE_2, height, sample2Width, controller);
        doc.drawLineX((y - sample2Width) / 2);
        doc.drawLineX((y - sample2Width) / 2 + sample2Width);
    }
}