package com.ducduong.print.util;

import com.ducduong.print.util.model.PageDirection;

import java.io.IOException;

public interface ResourceProvider {

    void provideNewPage(PageDirection direction);
    void close() throws IOException;

    PageDirection getDirection();
    void setDirection(PageDirection direction);
    float getPageWidth();
    float getPageHeight();

    void drawLineX(float x);
    void drawLineY(float y);
    // void drawBorder(float x, float y, float width, float height);
}
