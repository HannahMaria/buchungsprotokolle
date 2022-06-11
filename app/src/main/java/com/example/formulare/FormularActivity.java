package com.example.formulare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import com.example.formulare.formular_handler.ImageHandler;
import com.example.formulare.formular_handler.KindHandler;
import com.example.formulare.formular_handler.SignatureHandler;
import com.example.formulare.formular_handler.TiefbauHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FormularActivity extends AppCompatActivity {
    private ActivityFormularBinding binding;

    private static int SIGNATURE_RESULTS_CODE = 0;
    public static int PICKFILE_RESULT_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private HeaderHandler headerHandler;
    private BestandHandler bestandHandler;
    private ImageHandler imageHandler;
    private KindHandler kindHandler;
    private SignatureHandler signatureHandler;
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
        imageHandler = new ImageHandler(this, binding.formularImagesInc);
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
        //Das bekomme ich hin denke ich, aber wenn du zeit und lust hast kannst du damit auch schonmal weitermachen
        PdfDocument pdf = new PdfDocument();
        Paint myPaint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page1 = pdf.startPage(pageInfo);

        Canvas canvas = page1.getCanvas();


        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/Hello.pdf");
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
}