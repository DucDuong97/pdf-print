package com.ducduong.print;

import com.ducduong.print.crf.PdfCRFPrint;
import com.ducduong.print.util.model.PdfConfig;
import com.ducduong.print.util.*;
import com.ducduong.print.util.model.PageDirection;
import com.ducduong.print.pdfbox.*;
import com.ducduong.print.crf.pdfinfo.PdfInformationText;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class PdfPrintUtils implements Closeable {

    protected ResourceProvider resourceProvider;
    protected PositionController controller;
    protected StylingTextPrinter textPrinter;
    protected PdfPrintable<byte[]> imagePrinter;
    protected PdfTablePrinter tablePrinter;

    //------------------------------ Document Control ------------------------------------//

    @Override
    public void close() throws IOException {
        resourceProvider.close();
    }

    public void printTable(List<List> data, String sectionName) {
        PageDirection mainDirection = resourceProvider.getDirection();
        if (mainDirection != PageDirection.A4_LANDSCAPE) {
            controller.createNewPage(PageDirection.A4_LANDSCAPE);
        }
        printSectionName(sectionName);
        tablePrinter.printTableView(data, controller);
        resourceProvider.setDirection(mainDirection);
    }

    private static final int SECTION_MARGIN = 10;

    public void printSectionName(String name) {
        PdfPrintable<String> sectionPrinter = textPrinter.bold().withSize(1.5f);
        InformationPrinter<String> printer = new InformationPrinter<>(controller, sectionPrinter);
        printer.setMt(SECTION_MARGIN);
        printer.setMb(SECTION_MARGIN);
        printer.print(new PdfInformationText(name, 0));
    }

    public void printLineX (float x) {
        resourceProvider.drawLineX(x);
    }

    public void printLineY (float y) {
        resourceProvider.drawLineY(y);
    }


    public static class UtilsBuilder<B extends PdfPrintUtils> {

        protected PdfConfig config;
        protected Consumer<PositionController> decoration;

        public UtilsBuilder<B> withConfig (PdfConfig config) {
            this.config = config;
            return this;
        }

        public UtilsBuilder<B> withDecoration (Consumer<PositionController> decoration) {
            this.decoration = decoration;
            return this;
        }

        @SuppressWarnings("unchecked")
        protected B createInstance() {
            return (B) new PdfPrintUtils();
        }
        protected void configure(B b) {
            PDFBoxResourceProvider pdfBoxResource = new PDFBoxResourceProvider();
            b.resourceProvider = pdfBoxResource;
            b.textPrinter = new TextPrinter(pdfBoxResource);
            b.imagePrinter = new ImagePrinter(pdfBoxResource);
            b.tablePrinter = new PdfTablePrinter(pdfBoxResource);
            b.controller = new PositionController.ControllerBuilder(pdfBoxResource)
                            .withConfig(config)
                            .withDecoration(decoration)
                            .build();
        }
        protected void action(B b) {
            b.controller.createNewPage(config != null ? config.getPageDirection() : null);
        }

        public B build() {
            B result = createInstance();
            configure(result);
            action(result);
            return result;
        }
    }
}