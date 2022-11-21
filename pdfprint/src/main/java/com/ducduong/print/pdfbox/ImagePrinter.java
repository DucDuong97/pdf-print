package com.ducduong.print.pdfbox;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.util.PositionController;
import com.ducduong.print.util.ResourceProvider;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImagePrinter extends PdfPrintable<byte[]> {

    private final float IMAGE_MB = 0;

    private PDImageXObject pdImage;
    private float imgWidth;
    private float imgHeight;
    private PDFBoxResourceProvider pdfBoxResourceProvider;

    public ImagePrinter(PDFBoxResourceProvider PDFBoxResourceProvider) {
        this.pdfBoxResourceProvider = PDFBoxResourceProvider;
        this.mb = IMAGE_MB;
    }

    @Override
    public float calculateElemWidth(byte[] imgByteArray) {
        if (imgByteArray == null || imgByteArray.length == 0) return 0;
        processData(null, null, imgByteArray);
        return imgWidth;
    }

    @Override
    public float calculateElemWidth(byte[] imgByteArray, Float maxWidth, Float maxHeight) {
        if (imgByteArray == null || imgByteArray.length == 0) return 0;
        processData(maxWidth, maxHeight, imgByteArray);
        return imgWidth;
    }

    @Override
    public float calculateElemHeight(byte[] imgByteArray, float maxWidth) {
        if (imgByteArray == null || imgByteArray.length == 0) return 0;
        processData(maxWidth, null, imgByteArray);
        return imgHeight;
    }

    @Override
    public void printElem(byte[] imgByteArray, float maxWidth, float x, float y) {
        if (imgByteArray == null || imgByteArray.length == 0) return;
        processData(maxWidth, null, imgByteArray);
        printImage(x, y);
    }

    @Override
    public void printElem(byte[] imgByteArray, float maxWidth, Float maxHeight, float x, float y) {
        if (imgByteArray == null || imgByteArray.length == 0) return;
        processData(maxWidth, maxHeight, imgByteArray);
        print(imgByteArray, imgWidth, x, y);
    }

    private void processData(Float maxWidth, Float maxHeight, byte[] imgByteArray) {
        try {
            pdImage = pdfBoxResourceProvider.createImage(imgByteArray, null);
        } catch (IOException e) {
            e.printStackTrace();
            imgHeight = 0;
            imgWidth = 0;
            return;
        }
        imgWidth = pdImage.getWidth();
        imgHeight = pdImage.getHeight();
        if (maxWidth != null && imgWidth > maxWidth) {
            imgHeight = imgHeight * (maxWidth / imgWidth);
            imgWidth = maxWidth;
        }
        if (maxHeight != null && imgHeight > maxHeight) {
            imgWidth = imgWidth * (maxHeight / imgHeight);
            imgHeight = maxHeight;
        }
    }

    private void printImage(float x, float y) {
        try (PDPageContentStream contentStream = pdfBoxResourceProvider.createContentStream()) {
            contentStream.drawImage(pdImage, x, y - imgHeight, imgWidth, imgHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdImage = null;
        imgWidth = 0;
        imgHeight = 0;
    }

//    void printSideBarcode( String pseudonym) {
//        byte[] barcode = generateBarcodeByteArray(pseudonym);
//        try (PDPageContentStream contentStream = positionController.createContentStream()) {
//            PDImageXObject pdImage = PDFBoxResourceProvider.createImage(barcode, null);
//            if (pdImage == null) {
//                return;
//            }
//            //rotate image 90 degree
//            AffineTransform at = new AffineTransform(
//                    pdImage.getHeight(),
//                    0, 0, pdImage.getWidth(),
//                    50 + (float) (pdImage.getHeight() - 7) / 2,
//                    positionController.getPageHeight()/2 - pdImage.getHeight());
//            at.rotate(Math.toRadians(90));
//            //Drawing the image in the PDF document
//            contentStream.drawImage(pdImage, new Matrix(at));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static byte[] generateBarcodeByteArray(String barcode) {
        Code128Bean bean = new Code128Bean();
        bean.setHeight(10d);
        bean.doQuietZone(false);
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BitmapCanvasProvider provider =
                    new BitmapCanvasProvider(out, "image/x-png", 110,
                            BufferedImage.TYPE_BYTE_GRAY, false,
                            0);
            bean.generateBarcode(provider, barcode);
            provider.finish();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static byte[] generateQRCodeByteArray(String barcode) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            QRCode.from(barcode).withSize(200, 200).writeTo(out);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // function for library testing
    public static void printBarCode(String barcode, String fileName) {
        Code128Bean bean = new Code128Bean();
        bean.setHeight(10d);
        bean.doQuietZone(false);
        try {
            OutputStream out = new java.io.FileOutputStream(fileName);
            BitmapCanvasProvider provider =
                    new BitmapCanvasProvider(out, "image/x-png", 110,
                            BufferedImage.TYPE_BYTE_GRAY, false,
                            0);
            bean.generateBarcode(provider, barcode);
            provider.finish();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // function for library testing
    public static void printQRCode(String barcode, String fileName) {
        File file = QRCode.from(barcode).to(ImageType.PNG)
                .withSize(200, 200)
                .file();

        Path path = Paths.get(fileName);
        if ( Files.exists(path)) {
            try {
                Files.delete(path);
                Files.copy(file.toPath(), path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
