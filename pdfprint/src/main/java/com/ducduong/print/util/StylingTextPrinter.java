package com.ducduong.print.util;

public abstract class StylingTextPrinter extends PdfPrintable<String> {

    public abstract StylingTextPrinter bold();
    public abstract StylingTextPrinter withSize(float size);
}
