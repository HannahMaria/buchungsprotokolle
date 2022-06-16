package com.example.formulare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.formulare.formular_handler.BestandHandler;
import com.example.formulare.formular_handler.CustomerHandler;
import com.example.formulare.formular_handler.HeaderHandler;
import com.example.formulare.formular_handler.ImageHandler;
import com.example.formulare.formular_handler.KindHandler;
import com.example.formulare.formular_handler.SignatureHandler;
import com.example.formulare.formular_handler.TiefbauHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFGenerator {
    double faktor = 2.835;
    int pageWidth = (int) (210 * faktor);
    int pageHeight = (int) (297 * faktor);
    int betweenLines = 15;
    int overTitle = 30;
    float positionY = (float) (faktor * 19.5);
    float abstandRand = (float) (18 * faktor);
    Canvas canvas;

    FormularActivity formularActivity;
    SignatureHandler signatureHandler;
    HeaderHandler headerHandler;
    BestandHandler bestandHandler;
    ImageHandler imageHandler;
    KindHandler kindHandler;
    CustomerHandler customerHandler;
    TiefbauHandler tiefbauHandler;
    String filename;

    PDFGenerator(FormularActivity formularActivity, SignatureHandler signatureHandler, HeaderHandler headerHandler,
                 BestandHandler bestandHandler, KindHandler kindHandler, CustomerHandler customerHandler, TiefbauHandler tiefbauHandler, ImageHandler imageHandler) {
        this.formularActivity = formularActivity;
        this.signatureHandler = signatureHandler;
        this.headerHandler = headerHandler;
        this.bestandHandler = bestandHandler;
        this.imageHandler = imageHandler;
        this.kindHandler = kindHandler;
        this.customerHandler = customerHandler;
        this.tiefbauHandler = tiefbauHandler;
    }

    private Paint normalPaint(Paint.Align align) {
        Paint paint = new Paint();
        paint.setTextSize(10);
        paint.setTextAlign(align);
        paint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));

        return paint;

    }

    private Paint titlePaint(Paint.Align align) {
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setTextAlign(align);
        paint.setTypeface(Typeface.create("Arial", Typeface.BOLD));

        return paint;

    }

    private void drawRect(Canvas canvas, float left, float top, float right, float bottom) {
        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1);
        canvas.drawRect(left, top, right, bottom, strokePaint);

    }

    public File generatePDF() {
        positionY = (float) (faktor * 19.5);

        PdfDocument pdfCopy = new PdfDocument();
        PdfDocument pdf = new PdfDocument();
        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        PdfDocument.Page pageCopy = pdfCopy.startPage(pageInfo);

        File file;
        Canvas canvas = page.getCanvas();
        Canvas canvasCopy = pageCopy.getCanvas();
        try {
            float positionYCopy = positionY;
            if (!createHeader(canvasCopy)) return null;
            positionY = positionYCopy;
            createHeader(canvas);
            positionYCopy = positionY;

            if (!createFirst(canvasCopy)) return null;
            positionY = positionYCopy;
            createFirst(canvas);
            positionYCopy = positionY;

            if (!createSecond(canvasCopy)) return null;
            positionY = positionYCopy;
            createSecond(canvas);
            positionYCopy = positionY;

            if (!createThird(canvasCopy)) return null;
            positionY = positionYCopy;
            createThird(canvas);
            positionYCopy = positionY;

            if (!createFourth(canvasCopy)) return null;
            positionY = positionYCopy;
            createFourth(canvas);
            positionYCopy = positionY;

            if (!createSonstiges(canvasCopy)) return null;

            //maybe second Page
            if (positionY >= pageHeight - (faktor * 19.5 * 2)) {
                pdf.finishPage(page);
                pdfCopy.finishPage(pageCopy);
                positionY = (float) (faktor * 19.5);
                PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create();

                page = pdf.startPage(pageInfo2);
                pageCopy = pdfCopy.startPage(pageInfo2);
                canvas = page.getCanvas();
                canvasCopy = pageCopy.getCanvas();
            }else
            positionY = positionYCopy;
            createSonstiges(canvas);
            positionYCopy = positionY;

            if (!createSignatures(canvasCopy)) return null;

            if (positionY >= pageHeight - (faktor * 19.5 * 2)) {
                pdf.finishPage(page);
                pdfCopy.finishPage(pageCopy);
                positionY = (float) (faktor * 19.5);
                PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create();

                page = pdf.startPage(pageInfo2);
                pageCopy = pdfCopy.startPage(pageInfo2);
                canvas = page.getCanvas();
                canvasCopy = pageCopy.getCanvas();
            }else
            positionY = positionYCopy;
            createSignatures(canvas);


            //Bilder

            for (Uri uri : imageHandler.mArrayUri) {
                positionYCopy = positionY;

                createPictures(canvasCopy, uri);

                if (positionY >= pageHeight - (faktor * 19.5 * 2)) {
                    pdf.finishPage(page);
                    pdfCopy.finishPage(pageCopy);
                    positionY = (float) (faktor * 19.5);
                    PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create();

                    page = pdf.startPage(pageInfo2);
                    pageCopy = pdfCopy.startPage(pageInfo2);
                    canvas = page.getCanvas();
                    canvasCopy = pageCopy.getCanvas();
                }else{
                    positionY = positionYCopy;
                }
                createPictures(canvas, uri);
            }


            pdf.finishPage(page);
            filename = "/" + headerHandler.getProject() + "-" + headerHandler.getAdress();
            file = new File(formularActivity.getApplicationContext().getFilesDir(), filename + ".pdf");
            File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + ".pdf");
            int i = 0;
            while (file1.exists()) {
                if (i > 0)
                    filename = filename.substring(0, filename.length() - String.valueOf(i).length());
                i++;
                filename = filename + i;
                file = new File(formularActivity.getApplicationContext().getFilesDir(), filename + ".pdf");
                file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + ".pdf");
            }
            pdf.writeTo(new FileOutputStream(file));
            pdf.writeTo(new FileOutputStream(file1));
            Toast.makeText(formularActivity, "PDF erstellt - im Ordner Downloads!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(formularActivity, "PDF konnte nicht erstellt werden!", Toast.LENGTH_SHORT).show();
            return null;
        }
        pdf.close();
        return file;
    }

    private boolean createHeader(Canvas canvas) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");
        float startY = positionY;

        //Title
        Paint paint = new Paint();
        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create("Arial", Typeface.BOLD));

        canvas.drawText("Firma", abstandRand + 2, positionY += betweenLines, titlePaint(Paint.Align.LEFT));
        canvas.drawText(dateFormat.format(new Date()), pageWidth - abstandRand - 2, positionY, titlePaint(Paint.Align.RIGHT));


        canvas.drawText("Seith Energietechnik GmbH & Co KG", abstandRand + betweenLines, positionY + betweenLines, normalPaint(Paint.Align.LEFT));
        canvas.drawText("Bächlestr. 18", abstandRand + betweenLines, positionY + 40, normalPaint(Paint.Align.LEFT));
        canvas.drawText("76706 Dettenheim", abstandRand + betweenLines, positionY + 55, normalPaint(Paint.Align.LEFT));

        //Projekt
        if (headerHandler.getProject().isEmpty()) {
            Toast.makeText(formularActivity, "Projekt fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Projekt:" + "\t\t" + headerHandler.getProject(), (pageWidth / 2) + betweenLines, positionY += 25, normalPaint(Paint.Align.LEFT));

        //Auftrag
        if (headerHandler.getOrder().isEmpty()) {
            Toast.makeText(formularActivity, "Auftrag fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Auftrag:" + "\t\t" + headerHandler.getOrder(), (pageWidth / 2) + betweenLines, positionY += betweenLines, normalPaint(Paint.Align.LEFT));

        //Auftrag
        if (headerHandler.getAdress().isEmpty()) {
            Toast.makeText(formularActivity, "Adresse fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Adresse:" + "\t\t" + headerHandler.getAdress(), (pageWidth / 2) + betweenLines, positionY += betweenLines, normalPaint(Paint.Align.LEFT));

        drawRect(canvas, abstandRand, startY, pageWidth / 2, positionY += betweenLines);
        drawRect(canvas, pageWidth / 2, startY, pageWidth - abstandRand, positionY);

        //2.Quadrat
        startY = positionY;
        canvas.drawText("Begehungsprotokoll", pageWidth / 2, positionY += 20, paint);
        canvas.drawText("von", pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
        //Begeher
        if (headerHandler.getPerson().isEmpty()) {
            Toast.makeText(formularActivity, "Begeher fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText(headerHandler.getPerson(), (pageWidth / 2), positionY += betweenLines, normalPaint(Paint.Align.CENTER));
        drawRect(canvas, abstandRand, startY, pageWidth - abstandRand, positionY += betweenLines);

        return true;
    }

    private boolean createFirst(Canvas canvas) {
        //1. Abschnitt
        float startY = positionY;

        canvas.drawText("1. Kundeninformationen", abstandRand + 2, positionY += overTitle, titlePaint(Paint.Align.LEFT));

        //Eigentümer
        if (customerHandler.getOwner().isEmpty()) {
            Toast.makeText(formularActivity, "Eigentümer fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Eigentümer:" + "\t\t" + customerHandler.getOwner(), abstandRand + betweenLines, positionY += betweenLines, normalPaint(Paint.Align.LEFT));
        //E-Mail
        if (customerHandler.getEmail().isEmpty()) {
            Toast.makeText(formularActivity, "E-Mail fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("E-Mail:" + "\t\t" + customerHandler.getEmail(), abstandRand + betweenLines, positionY += betweenLines, normalPaint(Paint.Align.LEFT));

        //Datum
        if (!customerHandler.getNoDate() && !customerHandler.getYesDate()) {
            Toast.makeText(formularActivity, "Ausführungtermin Information fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (customerHandler.getNoDate())
                canvas.drawText("Kein Ausführungstermin notwendig!", pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
            else {
                if (customerHandler.getDateInput().isEmpty()) {
                    Toast.makeText(formularActivity, "Ausführungstermin fehlt!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                canvas.drawText("Ausführungstermin:" + "\t\t" + customerHandler.getDateInput(), abstandRand + betweenLines, positionY += betweenLines, normalPaint(Paint.Align.LEFT));
            }
        }

        drawRect(canvas, abstandRand, startY, pageWidth - abstandRand, positionY += betweenLines);
        return true;
    }

    private boolean createSecond(Canvas canvas) {
        //2. Abschnitt
        float startY = positionY;

        canvas.drawText("2. Räumlichkeiten", abstandRand + 2, positionY += overTitle, titlePaint(Paint.Align.LEFT));

        //Räumlichkeiten
        if (!kindHandler.privateManner() && !kindHandler.companyManner()) {
            Toast.makeText(formularActivity, "Räumlichkeiten Information fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (kindHandler.companyManner())
                canvas.drawText("Gewerbliches Gebäude", pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
            else {
                if (!kindHandler.onFamilyHouse() && !kindHandler.moreFamilyHouse()) {
                    Toast.makeText(formularActivity, "Haus-Art Information fehlt!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                canvas.drawText("Privates " + (kindHandler.moreFamilyHouse() ? " Mehrfamilien Haus" : " Einfamilien Haus"), pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
            }
        }

        drawRect(canvas, abstandRand, startY, pageWidth - abstandRand, positionY += betweenLines);
        return true;
    }

    private boolean createThird(Canvas canvas) {
        //3. Tiefbau
        float startY = positionY;

        canvas.drawText("3. Tiefbau", abstandRand + 2, positionY += overTitle, titlePaint(Paint.Align.LEFT));

        //Datum
        if (!tiefbauHandler.getNecessary() && !tiefbauHandler.getNotNecessary()) {
            Toast.makeText(formularActivity, "Tiefbau Information fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (tiefbauHandler.getNotNecessary())
                canvas.drawText("Kein Tiefbau notwendig!", pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
            else {
                if (!tiefbauHandler.getWithoutOber() && !tiefbauHandler.getAsphaltOber() && !tiefbauHandler.getPflasterOber()) {
                    Toast.makeText(formularActivity, "Tiefbau Information fehlt!", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (tiefbauHandler.getWithoutOber()) {
                        if (!tiefbauHandler.getWithoutOberLength().isEmpty()) {
                            canvas.drawText("Ohne Oberfläche:" + "\t\t" + tiefbauHandler.getWithoutOberLength() + " m Länge", abstandRand + betweenLines, positionY += betweenLines, normalPaint(Paint.Align.LEFT));
                        } else {
                            Toast.makeText(formularActivity, "Tiefbau Länge ohne Oberfläche fehlt!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    if (tiefbauHandler.getPflasterOber()) {
                        if (!tiefbauHandler.getPflasterOberLength().isEmpty()) {
                            canvas.drawText("Plaster:" + "\t\t" + tiefbauHandler.getPflasterOberLength() + " m Länge", abstandRand + betweenLines, positionY += betweenLines, normalPaint(Paint.Align.LEFT));
                        } else {
                            Toast.makeText(formularActivity, "Tiefbau Länge von Pflaster fehlt!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    if (tiefbauHandler.getAsphaltOber()) {
                        if (!tiefbauHandler.getAsphaltOberLength().isEmpty()) {
                            canvas.drawText("Asphalt:" + "\t\t" + tiefbauHandler.getAsphaltOberLength() + " m Länge", abstandRand + betweenLines, positionY += betweenLines, normalPaint(Paint.Align.LEFT));
                        } else {
                            Toast.makeText(formularActivity, "Tiefbau Länge von Asphalt fehlt!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }
            }
        }

        drawRect(canvas, abstandRand, startY, pageWidth - abstandRand, positionY += betweenLines);
        return true;
    }

    private boolean createFourth(Canvas canvas) {
        //4. Abschnitt
        float startY = positionY;

        canvas.drawText("4. Bestand", abstandRand + 2, positionY += overTitle, titlePaint(Paint.Align.LEFT));

        //Leitungswege
        if (!bestandHandler.yesCable() && !bestandHandler.noCable()) {
            Toast.makeText(formularActivity, "Leitungswege Information fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (bestandHandler.noCable())
                canvas.drawText("Keine störenden Leitungswege bekannt!", pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
            else {
                if (bestandHandler.getCableInput().isEmpty()) {
                    Toast.makeText(formularActivity, "Leitungswege Information fehlt!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                canvas.drawText("Störende Leitungswege: ", pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
                canvas.drawText(bestandHandler.getCableInput(), pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
            }
        }
        //Nutzbare Einführungen
        if (!bestandHandler.noIn() && !bestandHandler.yesIn()) {
            Toast.makeText(formularActivity, "Nutzbare Einführungen Information fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (bestandHandler.noIn())
                canvas.drawText("Keine nutzbaren Einführungen vorhanden!", pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
            else {
                if (!bestandHandler.getNoInComplete() && !bestandHandler.getYesInComplete()) {
                    Toast.makeText(formularActivity, "Nutzbare Einführungen vollständig Information fehlt!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                canvas.drawText(bestandHandler.getNoInComplete() ? "Unvollständige " : "Vollständige " + "nutzbaren Einführungen vorhanden!", pageWidth / 2, positionY += betweenLines, normalPaint(Paint.Align.CENTER));
            }

        }
        drawRect(canvas, abstandRand, startY, pageWidth - abstandRand, positionY += betweenLines);
        return true;
    }

    private boolean createSonstiges(Canvas canvas) {
        //Sonstige Angaben
        if (!formularActivity.sonstiges.getText().toString().isEmpty()) {
            float startY = positionY;

            TextPaint mTextPaint = new TextPaint();
            StaticLayout mTextLayout = new StaticLayout(formularActivity.sonstiges.getText().toString(), mTextPaint, (int) (pageWidth - (2 * abstandRand) - 30), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();


            canvas.drawText("Sonstige Angaben", abstandRand + 2, positionY += overTitle, titlePaint(Paint.Align.LEFT));

            canvas.translate(abstandRand + betweenLines, positionY += betweenLines);
            mTextLayout.draw(canvas);
            canvas.restore();
            drawRect(canvas, abstandRand, startY, pageWidth - abstandRand, positionY += mTextLayout.getHeight() + betweenLines);
        }
        return true;
    }

    private boolean createSignatures(Canvas canvas) {
        //Unterschriften
        float startY = positionY;

        canvas.drawText("Begeher", abstandRand + betweenLines, positionY += overTitle, titlePaint(Paint.Align.LEFT));
        canvas.drawText("Kunde", (pageWidth / 2) + betweenLines, positionY, titlePaint(Paint.Align.LEFT));


        if (signatureHandler.signatureBitmapBegeher == null) {
            Toast.makeText(formularActivity, "Unterschrift Begeher fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        Picture picture = new Picture();
        Canvas canvas2 = picture.beginRecording(signatureHandler.signatureBitmapBegeher.getWidth(), signatureHandler.signatureBitmapBegeher.getHeight());
        canvas2.drawBitmap(signatureHandler.signatureBitmapBegeher, null, new RectF(0f, 0f, (float) signatureHandler.signatureBitmapBegeher.getWidth(), (float) signatureHandler.signatureBitmapBegeher.getHeight()), null);
        picture.endRecording();
        canvas.drawPicture(picture, new Rect((int) abstandRand + 5, (int) (positionY += 15), (int) (int) (pageWidth / 2) - 10, (int) positionY + 75));


        if (signatureHandler.signatureBitmapKunde == null) {
            Toast.makeText(formularActivity, "Unterschrift Begeher fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }

        picture = new Picture();
        canvas2 = picture.beginRecording(signatureHandler.signatureBitmapKunde.getWidth(), signatureHandler.signatureBitmapKunde.getHeight());
        canvas2.drawBitmap(signatureHandler.signatureBitmapKunde, null, new RectF(0f, 0f, (float) signatureHandler.signatureBitmapKunde.getWidth(), (float) signatureHandler.signatureBitmapKunde.getHeight()), null);
        picture.endRecording();

        canvas.drawPicture(picture, new Rect((int) (pageWidth / 2) + 5, (int) positionY, (int) (pageWidth - abstandRand) - 10, (int) positionY + 75));

        canvas.drawText(headerHandler.getPerson(), abstandRand + betweenLines, positionY += betweenLines + 75, normalPaint(Paint.Align.LEFT));
        canvas.drawText(customerHandler.getOwner(), (pageWidth / 2) + betweenLines, positionY, normalPaint(Paint.Align.LEFT));
        drawRect(canvas, abstandRand, startY, pageWidth / 2, positionY += betweenLines);
        drawRect(canvas, pageWidth / 2, startY, pageWidth - abstandRand, positionY);
        return true;
    }

    private void createPictures(Canvas canvas, Uri uri) {
        //Bilder
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(formularActivity.getContentResolver(), uri);
            if (bitmap != null) {
                Picture picture = new Picture();
                Canvas canvas2 = picture.beginRecording(bitmap.getWidth(), bitmap.getHeight());
                canvas2.drawBitmap(bitmap, null, new RectF(0f, 0f, (float) bitmap.getWidth(), (float) bitmap.getHeight()), null);
                picture.endRecording();
                int width = picture.getWidth();
                int height = picture.getHeight();

                if (width > (pageWidth - abstandRand - 5)) {
                    double factor = (double) (pageWidth - abstandRand - 5) / width;
                    width = (int) (width * factor);
                    height = (int) (height * factor);
                }
                if (height > (pageHeight - ((faktor * 19.5 * 2)))) {
                    double factor = (double) (pageHeight - ((faktor * 19.5 * 2))) / height;
                    width = (int) (width * factor);
                    height = (int) (height * factor);
                }
                canvas.drawPicture(picture, new Rect((int) abstandRand + 5, (int) (positionY += 15), (int) (abstandRand + 5 + width), (int) (positionY += height)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
