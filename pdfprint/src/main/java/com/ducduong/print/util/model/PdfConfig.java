package com.ducduong.print.util.model;

import lombok.Data;

@Data
public class PdfConfig {

    private float mt = 40;
    private float mr = 40;
    private float mb = 40;
    private float ml = 40;
    private PageDirection pageDirection = PageDirection.A4_PORTRAIT;
}
