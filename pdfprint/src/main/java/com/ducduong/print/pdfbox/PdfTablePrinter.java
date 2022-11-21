package com.ducduong.print.pdfbox;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;
import be.quodlibet.boxable.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.ducduong.print.util.PositionController;
import com.ducduong.print.util.ResourceProvider;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfTablePrinter {

    private static final float TABLE_BOT_MARGIN = 20;

    PDFBoxResourceProvider resourceProvider;

    public PdfTablePrinter(PDFBoxResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public void printTableView(List<List> data, PositionController controller) {
        PDDocument doc = resourceProvider.getDoc();
        PDPage page = resourceProvider.getCurrentPage();
        float y = controller.getCurrentHeight();
        float yStartNewPage = controller.getUpperEdge();
        float marginSide = controller.getLeftEdge();
        float marginBot = controller.getLowerEdge();
        float tableWidth = (int) controller.getLineWidth();

        try {
            BaseTable dataTable = new BaseTable(y, yStartNewPage, marginBot, tableWidth,
                    marginSide, doc, page, true, true);
            DataTable t = new DataTable(dataTable, page);
            t.addListToTable(data, DataTable.HASHEADER);
            float newHeight = dataTable.draw();
            resourceProvider.setCurrentPage(dataTable.getCurrentPage());
            controller.setCurrentHeight(newHeight - TABLE_BOT_MARGIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
