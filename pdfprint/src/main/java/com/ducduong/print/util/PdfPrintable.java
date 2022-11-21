package com.ducduong.print.util;

public abstract class PdfPrintable<T> extends Boxable<T> {

    protected abstract void printElem(T t, float maxWidth, float x, float y);

    public void print(T t, float maxWidth, float x, float y) {
        printElem(t, maxWidth - ml - mr, x + ml, y - mt);
    }

    protected void printElem(T t, float maxWidth, Float maxHeight, float x, float y) {
        print(t, maxWidth, x, y);
    }

    public void print(T t, float maxWidth, Float maxHeight, float x, float y) {
        printElem(t, maxWidth - ml - mr, maxHeight - mt - mb, x + ml, y - mt);
    }

    public void printFromRight(T t, float y, float maxWidth, PositionController controller) {
        float width = Math.min(maxWidth, calculateWidth(t));
        print(t, width, controller.getRightEdge() - width, y);
    }

    public void printFromRight(T t, float maxWidth, Float maxHeight, float y, PositionController controller) {
        float width = calculateElemWidth(t, maxWidth, maxHeight);
        print(t, width, maxHeight, controller.getRightEdge() - width, y);
    }

    public void printCenter(T t, float y, float maxWidth, PositionController controller) {
        float textWidth = Math.min(maxWidth, calculateWidth(t));
        print(t, textWidth, controller.getLeftEdge() + (controller.getLineWidth() - textWidth) / 2, y);
    }
    public void printCenter   (T t, float maxWidth, Float maxHeight, float y, PositionController controller) {
        float textWidth = Math.min(maxWidth, calculateWidth(t));
        print(t, textWidth, maxHeight, controller.getLeftEdge() + (controller.getLineWidth() - textWidth) / 2, y);
    }
}
