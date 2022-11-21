package com.ducduong.print.crf.pdfquestion;

import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.pdfbox.FieldPrinter;

public abstract class PdfQuestion<T> {

    public static final int QUESTION_WIDTH = 120;

    protected final String name;
    protected final T obj;
    protected final String defaultValue;
    protected final boolean readOnly;
    protected final String note;
    protected final int level;

    public PdfQuestion(String name, T obj, String defaultValue, boolean readOnly, String note, int level) {
        this.name = name;
        this.obj = obj;
        this.defaultValue = defaultValue;
        this.readOnly = readOnly;
        this.note = note;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public int getLevel() {
        return level;
    }

    public abstract float calculateHeight(PdfPrintable<String> textPrinter);

    public abstract void print(float x, float y, FieldPrinter fieldPrinter);
}
