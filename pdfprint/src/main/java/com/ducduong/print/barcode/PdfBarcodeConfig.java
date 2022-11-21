package com.ducduong.print.barcode;

import lombok.Data;
import com.ducduong.print.util.model.PdfConfig;

@Data
public class PdfBarcodeConfig extends PdfConfig {

    private float width = 100;
    private float height = 100;
    private int amount = 10;
}
