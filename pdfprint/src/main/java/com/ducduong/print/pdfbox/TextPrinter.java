package com.ducduong.print.pdfbox;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import com.ducduong.print.util.ResourceProvider;
import com.ducduong.print.util.StylingTextPrinter;
import com.ducduong.print.util.PositionController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextPrinter extends StylingTextPrinter {

    public static final PDFont FONT = PDType1Font.HELVETICA;
    public static final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    public static final PDFont FONT_ITALIC = PDType1Font.HELVETICA_OBLIQUE;
    public static final int FONT_SIZE = 8;
    private static final int PARAGRAPH_BOTTOM_MARGIN = 8;

    private final PDFont font;
    private final float fontSize;
    private final float fontHeight;

    private final PDFBoxResourceProvider pdfBoxResourceProvider;

    public TextPrinter(PDFBoxResourceProvider pdfBoxResourceProvider) {
        this.pdfBoxResourceProvider = pdfBoxResourceProvider;
        font = FONT;
        fontSize = FONT_SIZE;
        this.fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        this.mb = PARAGRAPH_BOTTOM_MARGIN;
    }

    public TextPrinter(PDFont font, float fontSize, ResourceProvider PDFBoxResourceProvider) {
        this.font = font;
        this.fontSize = fontSize;
        pdfBoxResourceProvider = (PDFBoxResourceProvider) PDFBoxResourceProvider;
        this.fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        this.mb = PARAGRAPH_BOTTOM_MARGIN;
    }

    public TextPrinter bold() {
        return new TextPrinter(FONT_BOLD, this.fontSize, pdfBoxResourceProvider);
    }

    public TextPrinter withSize(float size) {
        return new TextPrinter(font, fontSize * size, pdfBoxResourceProvider);
    }

    @Override
    public void printElem(String string, float textWidth, float x, float y) {
        try (PDPageContentStream contentStream = pdfBoxResourceProvider.createContentStream()) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(x, y - fontHeight);
            // printing
            for (var text : splitToLines(standardizeString(string), textWidth)) {
                contentStream.showText(text);
                contentStream.newLineAtOffset(0, -fontHeight);
            }
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public float calculateElemWidth(String string) {
        try {
            return font.getStringWidth(standardizeString(string)) / 1000 * fontSize;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0f;
    }

    @Override
    public float calculateElemHeight(String string, float textWidth) {
        String[] texts = standardizeString(string).split("[\\r\\n]");
        int numOfLines = Arrays.stream(texts).mapToInt(text -> calculateNumOfLines(text, textWidth)).sum();
        float textHeight = fontHeight * numOfLines;
        return textHeight;
    }

    private String standardizeString(String string) {
        return string
                .replaceAll("\\u2265", ">=")
                .replaceAll("\\u2264", "<=");
    }

    private List<String> splitToLines(String string, float textWidth) {
        List<String> result = new ArrayList<>();
        // split text by carriage return
        String[] texts = string.split("[\\r\\n]");
        // split each paragraph into lines
        Arrays.stream(texts).forEach(text -> {
            int pointer = 0;
            int lastIndex = 0;
            for (int i : possibleWrapPoints(text)) {
                float width = calculateWidth(text.substring(pointer, i));
                if (width > textWidth) {
                    result.add(text.substring(pointer, lastIndex));
                    pointer = lastIndex;
                }
                lastIndex = i;
            }
            result.add(text.substring(pointer));
        });
        return result;
    }

    private int calculateNumOfLines(String text, float textWidth) {
        int numOfLines = 1;
        int poitner = 0;
        int lastIndex = 0;
        for (int i : possibleWrapPoints(text)) {
            float width = calculateWidth(text.substring(poitner, i));
            if (width > textWidth) {
                numOfLines++;
                poitner = lastIndex;
            }
            lastIndex = i;
        }
        return numOfLines;
    }

    private int[] possibleWrapPoints(String text) {
        String[] split = text.split("(?<=\\W)");
        int[] ret = new int[split.length];
        ret[0] = split[0].length();
        for (int i = 1; i < split.length; i++)
            ret[i] = ret[i - 1] + split[i].length();
        return ret;
    }
}
