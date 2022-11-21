package com.ducduong.print.pdfbox;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import com.ducduong.print.util.model.PageDirection;
import com.ducduong.print.util.ResourceProvider;
import org.springframework.web.context.annotation.SessionScope;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.getTempDirectory;
import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;

@SessionScope
public class PDFBoxResourceProvider implements ResourceProvider {

    private final PDDocument doc;
    private PageDirection direction;
    private PDPage currentPage;

    public PDFBoxResourceProvider() {
        doc = new PDDocument();
        direction = PageDirection.A4_PORTRAIT;
    }


    public void provideNewPage(PageDirection direction) {
        this.direction = direction != null ? direction : this.direction;
        PDRectangle mediaBox = new PDRectangle(this.direction.getWidth(), this.direction.getHeight());
        currentPage = new PDPage(mediaBox);
        doc.addPage(currentPage);
    }

    public void close() throws IOException {
        File pdfFile = new File(getTempDirectory(), "Questionnaire.pdf");
        doc.save(pdfFile);
        doc.close();
    }

    PDDocument getDoc() {
        return doc;
    }

    PDAcroForm provideForm() {
        PDAcroForm form = new PDAcroForm(doc);
        doc.getDocumentCatalog().setAcroForm(form);
        PDResources resources = new PDResources();
        resources.put(COSName.getPDFName("Helv"), PDType1Font.HELVETICA);
        form.setDefaultResources(resources);
        form.setDefaultAppearance("/Helv 12 Tf 0 g");
        return form;
    }

    PDAppearanceStream provideAS() {
        return  new PDAppearanceStream(doc);
    }

    PDPageContentStream createContentStream(PDAppearanceStream as) throws IOException {
        return new PDPageContentStream(doc, as);
    }

    PDImageXObject createImage(byte[] bytes, String name) throws IOException {
        return PDImageXObject.createFromByteArray(doc, bytes, null);
    }

    PDAnnotationWidget provideWidget() {
        return null;
    }

    PDPage getCurrentPage() {
        return currentPage;
    }

    void setCurrentPage(PDPage currentPage) {
        this.currentPage = currentPage;
    }

    PDFBoxResourceProvider onPage(PDPage page) {
        this.currentPage = page;
        return this;
    }

    PDPageContentStream createContentStream() throws IOException {
        return new PDPageContentStream(doc, currentPage, APPEND, true);
    }

    public PageDirection getDirection() {
        return direction;
    }

    public void setDirection(PageDirection direction) {
        this.direction = direction;
    }

    public float getPageWidth() {
        if (currentPage == null) {
            return direction.getWidth();
        }
        return currentPage.getMediaBox().getWidth();
    }

    public float getPageHeight() {
        if (currentPage == null) {
            return direction.getHeight();
        }
        return currentPage.getMediaBox().getHeight();
    }



    public void drawLineX(float x) {
        try(PDPageContentStream contentStream = createContentStream()) {
            contentStream.moveTo(x, 0);
            contentStream.lineTo(x, getPageHeight());
            contentStream.stroke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void drawLineY(float y) {
        try(PDPageContentStream contentStream = createContentStream()) {
            contentStream.moveTo(0, y);
            contentStream.lineTo(getPageWidth(), y);
            contentStream.stroke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawBorder(float x, float y, float width, float height) {
        try(PDPageContentStream contentStream = createContentStream()) {
            contentStream.moveTo(x, y);
            contentStream.lineTo(x + width, y);
            contentStream.stroke();

            contentStream.moveTo(x, y - height);
            contentStream.lineTo(x + width, y - height);
            contentStream.stroke();

            contentStream.moveTo(x, y);
            contentStream.lineTo(x, y - height);
            contentStream.stroke();

            contentStream.moveTo(x + width, y);
            contentStream.lineTo(x + width, y - height);
            contentStream.stroke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
