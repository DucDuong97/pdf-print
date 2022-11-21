package com.ducduong.print;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.ducduong.print.crf.PdfCRFPrint;
import com.ducduong.print.crf.pdfinfo.PdfExplainImage;
import com.ducduong.print.crf.pdfinfo.PdfInformationText;
import com.ducduong.print.crf.pdfinfo.PdfLink;
import com.ducduong.print.crf.pdfquestion.*;
import com.ducduong.print.crf.pdfquestion.Button;
import com.ducduong.print.pdfbox.ImagePrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionPrinterTest {

    private PdfCRFPrint pdfCRFPrint;

    @BeforeEach
    public void init() {
        pdfCRFPrint = new PdfCRFPrint.pdfCRFPrintBuilder().withPseudonym("Hello world").build();
    }

    @AfterEach
    public void close() {
        try {
            pdfCRFPrint.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrintFieldQuestion() {
        pdfCRFPrint.printPdfQuestion(
                new PdfField("A field", 20, "", false, "", 0));
        pdfCRFPrint.printPdfQuestion(
                new PdfField("A field with measurement Unit", 20, "50", true, "cm", 0));
        pdfCRFPrint.printPdfQuestion(
                new PdfField("read-only field", 20, "Read Only", true, "", 0));
        pdfCRFPrint.printPdfQuestion(
                new PdfField("A field with default", 20, "default", false, "", 0));
        pdfCRFPrint.printPdfQuestion(
                new PdfField("A 2-lines field", 30, "", false, "", 0));
    }

    @Test
    public void testPrintRadioButtonQuestion() {
        List<Button> answers = new ArrayList<>();
        answers.add(new Button("A", "A"));
        answers.add(new Button("A", "A"));
        answers.add(new Button("B", "B"));
        answers.add(new Button("C", "C"));

        pdfCRFPrint.printPdfQuestion(
                new PdfRadioButton("A radio question", answers, "", false, "", 0)
        );
        pdfCRFPrint.printPdfQuestion(
                new PdfRadioButton("A radio question", answers, "", false, "", 0)
        );
        pdfCRFPrint.printPdfQuestion(
                new PdfRadioButton("A radio question with answer", answers, "B", false, "", 0)
        );
        pdfCRFPrint.printPdfQuestion(
                new PdfRadioButton("A read-only radio question", answers, "B", true, "", 0)
        );
    }


    @Test
    public void testPrintCheckboxQuestion() {
        List<Button> answers = new ArrayList<>();
        answers.add(new Button("A", "A"));
        answers.add(new Button("B", "B"));
        answers.add(new Button("C", "C"));

        pdfCRFPrint.printPdfQuestion(
                new PdfCheckbox("A checkbox question", answers, "", true, "", 0)
        );
        pdfCRFPrint.printPdfQuestion(
                new PdfCheckbox("A read-only checkbox question", answers, "B", true, "", 0)
        );
        pdfCRFPrint.printPdfQuestion(
                new PdfCheckbox("A checkbox question with answer", answers, "A;C", true, "", 0)
        );
    }

    @Test
    public void testPrintDateQuestion() {
        pdfCRFPrint.printPdfQuestion(
                new PdfDate("Date question", null, "2020-11-22", false, "", 0)
        );
        pdfCRFPrint.printPdfQuestion(
                new PdfDate("Date Read-only question", null, "11-11-2020", true, "", 0)
        );
    }

    @Test
    public void testPrintDropdownQuestion() {
        List<Button> answers = new ArrayList<>();
        answers.add(new Button("A", "A"));
        answers.add(new Button("B", "B"));
        answers.add(new Button("C", "C"));
        pdfCRFPrint.printPdfQuestion(
                new PdfDropdown("DD question", answers, "B", false, "", 0)
        );
        pdfCRFPrint.printPdfQuestion(
                new PdfDropdown("DD Read-only question", answers, "B", true, "", 0)
        );
    }

    @Test
    public void testPrintInformation() {
        String pseudonym = "Hello world";

        pdfCRFPrint.printPseudonym("");
        pdfCRFPrint.printPseudonym(pseudonym);
        pdfCRFPrint.printHeaderContent("Header name", "Header value");

        pdfCRFPrint.printPatientBarcode(pseudonym);

        pdfCRFPrint.printPdfInformationText(new PdfInformationText("Hello World", 0));
        pdfCRFPrint.printPdfInformationText(new PdfInformationText("Hello World Level 1", 1));
        pdfCRFPrint.printPdfInformationText(new PdfLink("Link.abc", 0));
        pdfCRFPrint.printPdfExplainPicture(new PdfExplainImage(ImagePrinter.generateQRCodeByteArray(pseudonym), 0));
    }
}