package com.ducduong.print.pdfbox;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

import java.io.IOException;
import java.util.List;

public class PdfWidgetHandler {

    private final PDFBoxResourceProvider PDFBoxResourceProvider;

    public PdfWidgetHandler(PDFBoxResourceProvider PDFBoxResourceProvider) {
        this.PDFBoxResourceProvider = PDFBoxResourceProvider;
    }

    void setupFieldWidget(PDVariableText field, float x, float y, int boxWidth, int boxHeight) {
        PDRectangle rect = new PDRectangle(x, y, boxWidth, boxHeight);
        PDAnnotationWidget widget = field.getWidgets().get(0);
        widget.setRectangle(rect);
        // set black border
        // if you prefer defaults, just delete this code block
        PDAppearanceCharacteristicsDictionary fieldAppearance
                = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
        fieldAppearance.setBorderColour(new PDColor(new float[]{0,0,0}, PDDeviceRGB.INSTANCE));
        // for background color if you need
        // fieldAppearance.setBackground(new PDColor(new float[]{1,1,0}, PDDeviceRGB.INSTANCE));
        widget.setAppearanceCharacteristics(fieldAppearance);

        // make sure the widget annotation is visible on screen and paper
        widget.setPrinted(true);
        PDPage currentPage = PDFBoxResourceProvider.getCurrentPage();
        widget.setPage(currentPage);
        try {
            currentPage.getAnnotations().add(widget);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setupCheckboxWidget(PDCheckBox checkbox, float x, float y, int boxSize) {
        PDRectangle rect = new PDRectangle(x, y, boxSize, boxSize);
        PDAnnotationWidget widget = checkbox.getWidgets().get(0);
        widget.setRectangle(rect);
        widget.setPrinted(true);

        PDAppearanceCharacteristicsDictionary appearanceCharacteristics =
                new PDAppearanceCharacteristicsDictionary(new COSDictionary());
        appearanceCharacteristics.setBorderColour(new PDColor(new float[]{1, 0, 0}, PDDeviceRGB.INSTANCE));
        appearanceCharacteristics.setBackground(new PDColor(new float[]{1, 1, 0}, PDDeviceRGB.INSTANCE));
        // 8 = cross; 4 = checkmark; H = star; u = diamond; n = square, l = dot
        appearanceCharacteristics.setNormalCaption("8");
        widget.setAppearanceCharacteristics(appearanceCharacteristics);


        PDBorderStyleDictionary borderStyleDictionary = new PDBorderStyleDictionary();
        borderStyleDictionary.setWidth(1);
        borderStyleDictionary.setStyle(PDBorderStyleDictionary.STYLE_SOLID);
        widget.setBorderStyle(borderStyleDictionary);

        PDAppearanceDictionary ap = new PDAppearanceDictionary();
        widget.setAppearance(ap);
        PDAppearanceEntry normalAppearance = ap.getNormalAppearance();
        try {
            COSDictionary normalAppearanceDict = (COSDictionary) normalAppearance.getCOSObject();
            normalAppearanceDict.setItem(COSName.Off, createCheckBoxAS(widget, false));
            normalAppearanceDict.setItem(COSName.YES, createCheckBoxAS(widget, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PDPage currentPage = PDFBoxResourceProvider.getCurrentPage();
            widget.setPage(currentPage);
            currentPage.getAnnotations().add(widget);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    PDAnnotationWidget setupRadioButtonWidget(float x, float y, int boxSize, String value) throws IOException {
        PDAnnotationWidget widget =  createRadioButtonWidget(x, y,boxSize, value);
        try {
            PDPage currentPage = PDFBoxResourceProvider.getCurrentPage();
            widget.setPage(currentPage);
            currentPage.getAnnotations().add(widget);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return widget;
    }

    public void createLink(String uri, float x, float y, float width, float height) {
        PDAnnotationLink link = new PDAnnotationLink();
        link.setInvisible(false);
        final PDBorderStyleDictionary linkBorder = new PDBorderStyleDictionary ();
        linkBorder.setStyle (PDBorderStyleDictionary.STYLE_UNDERLINE);
        linkBorder.setWidth (1);
        link.setBorderStyle (linkBorder);
        PDActionURI actionUri = new PDActionURI();
        actionUri.setURI(uri);
        link.setAction(actionUri);
        PDRectangle pdRectangle = new PDRectangle();
        pdRectangle.setLowerLeftX(x);
        pdRectangle.setUpperRightY(y);
        pdRectangle.setUpperRightX(x + width);
        pdRectangle.setLowerLeftY(y - height);
        link.setRectangle(pdRectangle);
        try {
            PDFBoxResourceProvider.getCurrentPage().getAnnotations().add(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PDAppearanceStream createCheckBoxAS(PDAnnotationWidget widget, boolean on) throws IOException {
        PDRectangle rect = widget.getRectangle();
        PDAppearanceCharacteristicsDictionary appearanceCharacteristics;
        PDAppearanceStream yesAP = PDFBoxResourceProvider.provideAS();
        yesAP.setBBox(new PDRectangle(rect.getWidth(), rect.getHeight()));
        yesAP.setResources(new PDResources());
        PDPageContentStream yesAPCS = PDFBoxResourceProvider.createContentStream(yesAP);
        appearanceCharacteristics = widget.getAppearanceCharacteristics();
        PDColor backgroundColor = appearanceCharacteristics.getBackground();
        PDColor borderColor = appearanceCharacteristics.getBorderColour();
        float lineWidth = getLineWidth(widget);
        yesAPCS.setLineWidth(lineWidth); // border style (dash) ignored
        yesAPCS.setNonStrokingColor(backgroundColor);
        yesAPCS.addRect(0, 0, rect.getWidth(), rect.getHeight());
        yesAPCS.fill();
        yesAPCS.setStrokingColor(borderColor);
        yesAPCS.addRect(lineWidth / 2, lineWidth / 2, rect.getWidth() - lineWidth, rect.getHeight() - lineWidth);
        yesAPCS.stroke();

        if (on) {
            yesAPCS.setStrokingColor(0f);
            yesAPCS.moveTo(lineWidth * 2, rect.getHeight() - lineWidth * 2);
            yesAPCS.lineTo(rect.getWidth() - lineWidth * 2, lineWidth * 2);
            yesAPCS.moveTo(rect.getWidth() - lineWidth * 2, rect.getHeight() - lineWidth * 2);
            yesAPCS.lineTo(lineWidth * 2, lineWidth * 2);
            yesAPCS.stroke();
        }
        yesAPCS.close();
        return yesAP;
    }

    private PDAnnotationWidget createRadioButtonWidget(float x, float y, int boxSize, String buttonValue) throws IOException {
        PDAnnotationWidget widget = new PDAnnotationWidget();
        PDAppearanceCharacteristicsDictionary appearanceCharacteristics = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
        appearanceCharacteristics.setBorderColour(new PDColor(new float[] { 1, 0, 0 }, PDDeviceRGB.INSTANCE));
        appearanceCharacteristics.setBackground(new PDColor(new float[] {0, 1, 0.3f}, PDDeviceRGB.INSTANCE));
        widget.setRectangle(new PDRectangle(x, y, boxSize, boxSize));
        widget.setAppearanceCharacteristics(appearanceCharacteristics);
        PDBorderStyleDictionary borderStyleDictionary = new PDBorderStyleDictionary();
        borderStyleDictionary.setWidth(1);
        borderStyleDictionary.setStyle(PDBorderStyleDictionary.STYLE_SOLID);
        widget.setBorderStyle(borderStyleDictionary);

        COSDictionary apNDict = new COSDictionary();
        apNDict.setItem(COSName.Off, createRadioButtonAppearanceStream(widget, false));
        apNDict.setItem(buttonValue, createRadioButtonAppearanceStream(widget, true));

        PDAppearanceDictionary appearance = new PDAppearanceDictionary();
        PDAppearanceEntry appearanceNEntry = new PDAppearanceEntry(apNDict);
        appearance.setNormalAppearance(appearanceNEntry);
        widget.setAppearance(appearance);
        widget.setAppearanceState("Off"); // don't forget this, or button will be invisible
        return widget;
    }

    private PDAppearanceStream createRadioButtonAppearanceStream(
            PDAnnotationWidget widget, boolean on) throws IOException {
        PDRectangle rect = widget.getRectangle();
        PDAppearanceStream onAP = PDFBoxResourceProvider.provideAS();
        onAP.setBBox(new PDRectangle(rect.getWidth(), rect.getHeight()));
        PDPageContentStream onAPCS = PDFBoxResourceProvider.createContentStream(onAP);

        PDAppearanceCharacteristicsDictionary appearanceCharacteristics = widget.getAppearanceCharacteristics();
        PDColor backgroundColor = appearanceCharacteristics.getBackground();
        PDColor borderColor = appearanceCharacteristics.getBorderColour();
        float lineWidth = getLineWidth(widget);
        onAPCS.setLineWidth(lineWidth); // border style (dash) ignored
        onAPCS.setNonStrokingColor(backgroundColor);
        float radius = Math.min(rect.getWidth() / 2, rect.getHeight() / 2);
        drawCircle(onAPCS, rect.getWidth() / 2, rect.getHeight() / 2, radius);
        onAPCS.fill();
        onAPCS.setStrokingColor(borderColor);
        drawCircle(onAPCS, rect.getWidth() / 2, rect.getHeight() / 2, radius - lineWidth / 2);
        onAPCS.stroke();

        if (on) {
            onAPCS.setNonStrokingColor(0f);
            drawCircle(onAPCS, rect.getWidth() / 2, rect.getHeight() / 2, (radius - lineWidth) / 2);
            onAPCS.fill();
        }

        onAPCS.close();
        return onAP;
    }

    private static float getLineWidth(PDAnnotationWidget widget) {
        PDBorderStyleDictionary bs = widget.getBorderStyle();

        if (bs != null) {
            return bs.getWidth();
        }
        return 1;
    }

    private static void drawCircle(PDPageContentStream cs, float x, float y, float r) throws IOException {
        float magic = r * 0.551784f;
        cs.moveTo(x, y + r);
        cs.curveTo(x + magic, y + r, x + r, y + magic, x + r, y);
        cs.curveTo(x + r, y - magic, x + magic, y - r, x, y - r);
        cs.curveTo(x - magic, y - r, x - r, y - magic, x - r, y);
        cs.curveTo(x - r, y + magic, x - magic, y + r, x, y + r);
        cs.closePath();
    }

}
