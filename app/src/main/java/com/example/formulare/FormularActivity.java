package com.example.formulare;

import android.Manifest;
import android.content.pm.PackageManager;
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
    private PDFGenerator pdfGenerator;
    private CustomerHandler customerHandler;
    private TiefbauHandler tiefbauHandler;


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
        customerHandler = new CustomerHandler(binding.formularCustomerInc);
        tiefbauHandler = new TiefbauHandler(binding.formularTiefbauInc);
        pdfGenerator = new PDFGenerator(this, signatureHandler, headerHandler, bestandHandler, kindHandler, customerHandler, tiefbauHandler);
    }


    public void createPDFButton() {
        // below code is used for
        // checking our permissions.
        if (checkPermission()) {
            Toast.makeText(this, "Berechtigungen stimmen.", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        binding.buttonGeneratePdf.setOnClickListener(v -> pdfGenerator.generatePDF());
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
                Toast.makeText(FormularActivity.this, "Bilder können nicht in externen Speicher abgelegt werden", Toast.LENGTH_SHORT).show();
            }
        }
    }

}