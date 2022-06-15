package com.example.formulare;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.example.formulare.formular_handler.BestandHandler;
import com.example.formulare.formular_handler.CustomerHandler;
import com.example.formulare.formular_handler.HeaderHandler;
import com.example.formulare.formular_handler.KindHandler;
import com.example.formulare.formular_handler.SignatureHandler;
import com.example.formulare.formular_handler.TiefbauHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFGenerator {
    int pageWidth = 2480;
    int pageHeight = 3508;
    FormularActivity formularActivity;
    SignatureHandler signatureHandler;
    HeaderHandler headerHandler;
    BestandHandler bestandHandler;
    KindHandler kindHandler;
    CustomerHandler customerHandler;
    TiefbauHandler tiefbauHandler;

    PDFGenerator(FormularActivity formularActivity, SignatureHandler signatureHandler, HeaderHandler headerHandler,
                 BestandHandler bestandHandler, KindHandler kindHandler, CustomerHandler customerHandler, TiefbauHandler tiefbauHandler) {
        this.formularActivity = formularActivity;
        this.signatureHandler = signatureHandler;
        this.headerHandler = headerHandler;
        this.bestandHandler = bestandHandler;
        this.kindHandler = kindHandler;
        this.customerHandler = customerHandler;
        this.tiefbauHandler = tiefbauHandler;
    }

    public void generatePDF() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");
        //Das bekomme ich hin denke ich, aber wenn du zeit und lust hast kannst du damit auch schonmal weitermachen
        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();
        Paint strokePaint = new Paint();
        Paint titlePaint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page1 = pdf.startPage(pageInfo);

        Canvas canvas = page1.getCanvas();
        try {
            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(120);
            canvas.drawText("Begehungsprotokoll", pageWidth / 2, 160, titlePaint);

            //DATum
            titlePaint.setTextSize(70);
            titlePaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(dateFormat.format(new Date()), pageWidth - 40, 160, titlePaint);

            //1. Abschnitt
            if (!createFirst(canvas, paint, strokePaint)) return;

            //2. Abschnitt
            if (!createSecond(canvas, paint, strokePaint)) return;

            //3. Abschnitt
            if (!createThird(canvas, paint, strokePaint)) return;
            //3. Abschnitt
            if (!createFourth(canvas, paint, strokePaint)) return;

            //Unterschriften
            paint.setTextSize(80);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Begeher", 40, pageHeight - 700, paint);
            canvas.drawText("Kunde", (pageWidth / 2) + 20, pageHeight - 700, paint);

            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(2);
            canvas.drawRect(40, pageHeight - 680, (pageWidth / 2) - 20, pageHeight - 80, strokePaint);
            canvas.drawRect((pageWidth / 2) + 20, pageHeight - 680, pageWidth - 40, pageHeight - 80, strokePaint);

            paint.setTextSize(70);
            paint.setTextAlign(Paint.Align.CENTER);

            if (signatureHandler.signatureBitmapBegeher == null) {
                Toast.makeText(formularActivity, "Unterschrift Begeher fehlt!", Toast.LENGTH_SHORT).show();
                return;
            }
            canvas.drawBitmap(signatureHandler.signatureBitmapBegeher, 50, pageHeight - 600, paint);
            canvas.drawText(headerHandler.getPerson(), 240, pageHeight - 100, paint);

            if (signatureHandler.signatureBitmapKunde == null) {
                Toast.makeText(formularActivity, "Unterschrift Begeher fehlt!", Toast.LENGTH_SHORT).show();
                return;
            }
            canvas.drawBitmap(signatureHandler.signatureBitmapKunde, (pageWidth / 2) + 40, pageHeight - 600, paint);
            canvas.drawText(customerHandler.getOwner(), (pageWidth / 2) + 160, pageHeight - 100, paint);

            pdf.finishPage(page1);

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/" + headerHandler.getProject() + "-" + headerHandler.getAdress() + System.currentTimeMillis() + ".pdf");

            pdf.writeTo(new FileOutputStream(file));
            Toast.makeText(formularActivity, "PDF erstellt - im Ordner Downloads!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(formularActivity, "PDF konnte nicht erstellt werden!", Toast.LENGTH_SHORT).show();

        }
        pdf.close();
    }

    private boolean createFirst(Canvas canvas, Paint paint, Paint strokePaint) {
        //1. Abschnitt
        paint.setTextSize(90);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("1.Allgemeine Informationen", 40, 400, paint);

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);
        canvas.drawRect(40, 420, pageWidth - 40, 840, strokePaint);

        paint.setTextSize(70);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);

        if (headerHandler.getProject().isEmpty()) {
            Toast.makeText(formularActivity, "Projekt fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Projekt:", 80, 500, paint);
        canvas.drawText(headerHandler.getProject(), pageWidth / 2, 500, paint);

        if (headerHandler.getOrder().isEmpty()) {
            Toast.makeText(formularActivity, "Auftrag fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Auftrag:", 80, 580, paint);
        canvas.drawText(headerHandler.getOrder(), pageWidth / 2, 580, paint);

        if (customerHandler.getOwner().isEmpty()) {
            Toast.makeText(formularActivity, "Eigentümer fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Eigentümer:", 80, 660, paint);
        canvas.drawText(customerHandler.getOwner(), pageWidth / 2, 660, paint);

        if (customerHandler.getOwner().isEmpty()) {
            Toast.makeText(formularActivity, "E-Mail fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        //TODO:NächsteZeile
        canvas.drawText("Eigentümer E-Mail:", 80, 660, paint);
        canvas.drawText(customerHandler.getEmail(), pageWidth / 2, 660, paint);

        if (headerHandler.getPerson().isEmpty()) {
            Toast.makeText(formularActivity, "Begeher fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Begeher:", 80, 740, paint);
        canvas.drawText(headerHandler.getPerson(), pageWidth / 2, 740, paint);

        if (headerHandler.getAdress().isEmpty()) {
            Toast.makeText(formularActivity, "Adresse fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Adresse:", 80, 820, paint);
        canvas.drawText(headerHandler.getAdress(), pageWidth / 2, 820, paint);
        return true;
    }

    private boolean createSecond(Canvas canvas, Paint paint, Paint strokePaint) {
        //2. Abschnitt
        paint.setTextSize(90);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("2.Räumlichkeiten", 40, 1000, paint);

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);
        canvas.drawRect(40, 1020, pageWidth - 40, 1120, strokePaint);

        paint.setTextSize(70);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawText("Art:", 80, 1100, paint);
        String text = kindHandler.privateManner() ? "Privat" : "Gewerblich";
        canvas.drawText(text, pageWidth / 3, 1100, paint);

        if (!kindHandler.privateManner() && !kindHandler.companyManner()) {
            Toast.makeText(formularActivity, "Art fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (kindHandler.privateManner()) {
            if (!kindHandler.moreFamilyHouse() && !kindHandler.onFamilyHouse()) {
                Toast.makeText(formularActivity, "Haus Art fehlt!", Toast.LENGTH_SHORT).show();
                return false;
            }
            canvas.drawText(kindHandler.moreFamilyHouse() ? "Mehrfamilienhaus" : "Einfamilienhaus", pageWidth / 3 * 2, 1100, paint);
        }

        return true;
    }

    private boolean createThird(Canvas canvas, Paint paint, Paint strokePaint) {
        //3. Abschnitt
        paint.setTextSize(90);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("3.Tiefbau", 40, 1280, paint);

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);
        canvas.drawRect(40, 1300, pageWidth - 40, 1560, strokePaint);

        paint.setTextSize(70);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawText("Tiefbau notwendig?", 80, 1380, paint);
        String text = tiefbauHandler.getNecessary() ? "Ja" : "Nein";
        canvas.drawText(text, pageWidth / 3, 1380, paint);
        if (!tiefbauHandler.getNecessary() && !tiefbauHandler.getNotNecessary()) {
            Toast.makeText(formularActivity, "Tiefbau Information fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
     /*   if (tiefbauHandler.getNecessary_RB()) {
            if (tiefbauHandler.getLength_EDT().isEmpty()) {
                Toast.makeText(formularActivity, "Länge fehlt!", Toast.LENGTH_SHORT).show();
                return false;
            }
            canvas.drawText("Länge:", pageWidth / 2, 1420, paint);
            canvas.drawText(tiefbauHandler.getLength_EDT(), pageWidth / 4 * 3, 1420, paint);
            if (tiefbauHandler.getOverground_EDT().isEmpty()) {
                Toast.makeText(formularActivity, "Oberflächenart fehlt!", Toast.LENGTH_SHORT).show();
                return false;
            }
            canvas.drawText("Oberflächenart:", 80, 1460, paint);
            canvas.drawText(tiefbauHandler.getOverground_EDT(), 80, 1540, paint);
        }*/
        return true;
    }

    private boolean createFourth(Canvas canvas, Paint paint, Paint strokePaint) {
        //4. Abschnitt
        paint.setTextSize(90);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("4.Bestand", 40, 1720, paint);

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);
        canvas.drawRect(40, 1740, pageWidth - 40, 1920, strokePaint);

        paint.setTextSize(70);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        if (!bestandHandler.noCable() && !bestandHandler.yesCable()) {
            Toast.makeText(formularActivity, "Leitungswege Information fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Bekannte Leitungswege, welche den geplanten Trassenverlauf stören:", 80, 1820, paint);
        if (bestandHandler.yesCable()) {
            if (!bestandHandler.getCableInput().isEmpty()) {
                Toast.makeText(formularActivity, "Nutzbare Einführungen Information fehlt!", Toast.LENGTH_SHORT).show();
                return false;
            }
            //TODO: nächste Zeile
            canvas.drawText("Ja - " + bestandHandler.getCableInput(), pageWidth / 3 * 2, 1820, paint);

        } else {
            canvas.drawText("Nein", pageWidth / 3 * 2, 1820, paint);
        }

        if (!bestandHandler.yesIn() && !bestandHandler.noIn()) {
            Toast.makeText(formularActivity, "Nutzbare Einführungen Information fehlt!", Toast.LENGTH_SHORT).show();
            return false;
        }
        canvas.drawText("Nutzbare Einführungen:", 80, 1900, paint);
        String text = bestandHandler.yesIn() ? "Ja" : "Nein";
        canvas.drawText(text, pageWidth / 2, 1900, paint);


        return true;
    }
}
