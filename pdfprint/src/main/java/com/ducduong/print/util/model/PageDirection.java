package com.ducduong.print.util.model;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public enum PageDirection {

    A4_LANDSCAPE {
        @Override
        public float getWidth() {
            return PDRectangle.A4.getHeight();
        }

        @Override
        public float getHeight() {
            return PDRectangle.A4.getWidth();
        }
    },
    A4_PORTRAIT {
        @Override
        public float getWidth() {
            return PDRectangle.A4.getWidth();
        }

        @Override
        public float getHeight() {
            return PDRectangle.A4.getHeight();
        }
    };

    public abstract float getWidth();
    public abstract float getHeight();
}
