package com.ducduong.print.util;

import com.ducduong.print.util.model.PageDirection;
import com.ducduong.print.util.model.PdfConfig;

import java.util.function.Consumer;

public class PositionController {

    private final ResourceProvider resourceProvider;
    private float mt;
    private float mr;
    private float mb;
    private float ml;

    private float currentHeight;
    private Consumer<PositionController> decoratePage;
    private Integer pageCount;

    private PositionController(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public void createNewPage(PageDirection direction) {
        resourceProvider.provideNewPage(direction);
        currentHeight = getUpperEdge();
        pageCount++;
        decoratePage.accept(this);
    }

    public void setCurrentHeight(float currentHeight) {
        this.currentHeight = currentHeight;
    }

    public float getCurrentHeight() {
        return currentHeight;
    }

    public void adjustCurrentHeight(float adjust) {
        currentHeight -= adjust;
    }

    public void createNewPageIfNeeded(float height) {
        if (currentHeight - height < getLowerEdge()) {
            createNewPage(null);
        }
    }

    public float getUpperEdge() {
        return resourceProvider.getPageHeight() - mt;
    }

    public float getLowerEdge() {
        return mb;
    }

    public float getRightEdge() {
        return resourceProvider.getPageWidth() - mr;
    }

    public float getLeftEdge() {
        return ml;
    }

    public float getLineWidth() {
        return resourceProvider.getPageWidth() - ml - mr;
    }

    public void setMt(float mt) {
        this.mt = mt;
        float newUpperEdge = getUpperEdge();
        if (currentHeight > newUpperEdge) {
            currentHeight = newUpperEdge;
        }
    }

    public void setMr(float mr) {
        this.mr = mr;
    }

    public void setMb(float mb) {
        this.mb = mb;
    }

    public void setMl(float ml) {
        this.ml = ml;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public static class ControllerBuilder {

        public static final int MARGIN = 60;

        private final ResourceProvider resourceProvider;
        private PdfConfig config;
        private Consumer<PositionController> decoratePage;

        public ControllerBuilder(ResourceProvider resourceProvider) {
            this.resourceProvider = resourceProvider;
        }

        public ControllerBuilder withConfig(PdfConfig config) {
            this.config = config;
            return this;
        }

        public ControllerBuilder withDecoration(Consumer<PositionController> decoration) {
            this.decoratePage = decoration;
            return this;
        }

        public PositionController build() {
            if (decoratePage == null) {
                decoratePage = (ignored) -> {};
            }
            PositionController controller = new PositionController(this.resourceProvider);
            controller.pageCount = 0;
            controller.decoratePage = this.decoratePage;
            if (config == null) {
                controller.mt = MARGIN;
                controller.mr = MARGIN;
                controller.mb = MARGIN;
                controller.ml = MARGIN;
            } else {
                controller.mt = config.getMt();
                controller.mr = config.getMr();
                controller.mb = config.getMb();
                controller.ml = config.getMl();
            }
            return controller;
        }
    }
}
