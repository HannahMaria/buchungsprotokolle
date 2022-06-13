package com.example.formulare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.formulare.databinding.ActivityFormularBinding;
import com.example.formulare.formular_handler.BestandHandler;
import com.example.formulare.formular_handler.HeaderHandler;
import com.example.formulare.formular_handler.KindHandler;
import com.example.formulare.formular_handler.SignatureHandler;
import com.example.formulare.formular_handler.TiefbauHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormularActivity extends AppCompatActivity {
    private ActivityFormularBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private HeaderHandler headerHandler;
    private BestandHandler bestandHandler;
    //  private ImageHandler imageHandler;
    private KindHandler kindHandler;
    private SignatureHandler signatureHandler;
    private TiefbauHandler tiefbauHandler;
    int pageWidth = 2480;
    int pageHeight = 3508;
    Bitmap bmp, scaledbmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formular);
        binding = ActivityFormularBinding.inflate(getLayoutInflater());
//        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());

        createPDFButton();
        initHandler();
    }

    private void initHandler() {
        headerHandler = new HeaderHandler(binding.formularHeaderInc);
        bestandHandler = new BestandHandler(binding.formularBestandInc);
        // imageHandler = new ImageHandler(this, binding.formularImagesInc);
        kindHandler = new KindHandler(binding.formularKindInc);
        signatureHandler = new SignatureHandler(this, binding.formularSignatureInc);
        tiefbauHandler = new TiefbauHandler(binding.formularTiefbauInc);
    }


    public void createPDFButton() {
        // below code is used for
        // checking our permissions.
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        binding.buttonGeneratePdf.setOnClickListener(v -> generatePDF());
    }

    private void generatePDF() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy");
        //Das bekomme ich hin denke ich, aber wenn du zeit und lust hast kannst du damit auch schonmal weitermachen
        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();
        Paint strokePaint = new Paint();
        Paint titlePaint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page1 = pdf.startPage(pageInfo);

        Canvas canvas = page1.getCanvas();

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(120);
        canvas.drawText("Buchungsprotokoll", pageWidth / 2, 160, titlePaint);

        //DATum
        titlePaint.setTextSize(70);
        titlePaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(dateFormat.format(new Date()), pageWidth - 40, 160, titlePaint);

        //1. Abschnitt
        createFirst(canvas, paint, strokePaint);

        //2. Abschnitt
        createSecond(canvas, paint, strokePaint);

        //3. Abschnitt
        createThird(canvas, paint, strokePaint);
        //3. Abschnitt
        createFourth(canvas, paint, strokePaint);

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
        canvas.drawBitmap(signatureHandler.signatureBitmapBegeher, 50, pageHeight - 600, paint);
        canvas.drawText(headerHandler.getPerson(), 240, pageHeight - 100, paint);

        canvas.drawBitmap(signatureHandler.signatureBitmapKunde, (pageWidth / 2) + 40, pageHeight - 600, paint);
        canvas.drawText(headerHandler.getOwner(), (pageWidth / 2) + 160, pageHeight - 100, paint);

        pdf.finishPage(page1);


        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/" + headerHandler.getProject() + "-" + headerHandler.getConnection() + System.currentTimeMillis() + ".pdf");
        try {
            pdf.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdf.close();

    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS_STORAGE[0]);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS_STORAGE[1]);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FormularActivity.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createFirst(Canvas canvas, Paint paint, Paint strokePaint) {
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

        canvas.drawText("Projekt:", 80, 500, paint);
        canvas.drawText(headerHandler.getProject(), pageWidth / 2, 500, paint);

        canvas.drawText("Auftrag:", 80, 580, paint);
        canvas.drawText(headerHandler.getOrder(), pageWidth / 2, 580, paint);


        canvas.drawText("Eigentümer:", 80, 660, paint);
        canvas.drawText(headerHandler.getOwner(), pageWidth / 2, 660, paint);

        canvas.drawText("Begeher:", 80, 740, paint);
        canvas.drawText(headerHandler.getPerson(), pageWidth / 2, 740, paint);


        canvas.drawText("Anschluss:", 80, 820, paint);
        canvas.drawText(headerHandler.getConnection(), pageWidth / 2, 820, paint);
    }

    private void createSecond(Canvas canvas, Paint paint, Paint strokePaint) {
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

        if (kindHandler.privateManner())
            canvas.drawText(kindHandler.moreFamilyHouse() ? "Mehrfamilienhaus" : "Einfamilienhaus", pageWidth / 3 * 2, 1100, paint);

    }

    private void createThird(Canvas canvas, Paint paint, Paint strokePaint) {
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
        String text = tiefbauHandler.getNecessary_RB() ? "Ja" : "Nein";
        canvas.drawText(text, pageWidth / 3, 1380, paint);

        if (tiefbauHandler.getNecessary_RB()) {
            canvas.drawText("Länge:", pageWidth / 2, 1420, paint);
            canvas.drawText(tiefbauHandler.getLength_EDT(), pageWidth / 4 * 3, 1420, paint);

            canvas.drawText("Oberflächenart:", 80, 1460, paint);
            canvas.drawText(tiefbauHandler.getOverground_EDT(), 80, 1540, paint);
        }
    }

    private void createFourth(Canvas canvas, Paint paint, Paint strokePaint) {
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

        canvas.drawText("Leitungen bekannt?:", 80, 1820, paint);
        String text = bestandHandler.yesCable() ? "Ja" : "Nein";
        canvas.drawText(text, pageWidth / 2, 1820, paint);

        canvas.drawText("Nutzbare Einführungen:", 80, 1900, paint);
        text = bestandHandler.yesIn() ? "Ja" : "Nein";
        canvas.drawText(text, pageWidth / 2, 1900, paint);

    }
}