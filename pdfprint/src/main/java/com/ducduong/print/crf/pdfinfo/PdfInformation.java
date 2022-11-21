package com.ducduong.print.crf.pdfinfo;

import com.ducduong.print.util.PdfPrintable;

public abstract class PdfInformation<T> {

    protected final T obj;
    protected final int level;

    public PdfInformation(T obj, int level) {
        this.obj = obj;
        this.level = level;
    }

    public abstract float calculateHeight(float width, PdfPrintable<T>  printer);

    public abstract void print(float x, float y, float width, PdfPrintable<T> printer);

    public T getObj() {
        return obj;
    }

    public int getLevel() {
        return level;
    }
}
