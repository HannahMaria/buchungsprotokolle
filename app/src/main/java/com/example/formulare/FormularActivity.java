package com.example.formulare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.formulare.databinding.ActivityFormularBinding;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FormularActivity extends AppCompatActivity {
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
    private ActivityFormularBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private HeaderHandler headerHandler;
    private BestandHandler bestandHandler;
      private ImageHandler imageHandler;
    private KindHandler kindHandler;
    private SignatureHandler signatureHandler;
    private PDFGenerator pdfGenerator;
    private CustomerHandler customerHandler;
    private TiefbauHandler tiefbauHandler;
    public EditText sonstiges;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formular);
        binding = ActivityFormularBinding.inflate(getLayoutInflater());
//        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());

        createPDFButton();
        createSendButton();
        initHandler();
    }

    private void initHandler() {
        headerHandler = new HeaderHandler(binding.formularHeaderInc);
        bestandHandler = new BestandHandler(binding.formularBestandInc);
        imageHandler = new ImageHandler(this, binding.formularImagesInc);
        kindHandler = new KindHandler(binding.formularKindInc);
        signatureHandler = new SignatureHandler(this, binding.formularSignatureInc);
        customerHandler = new CustomerHandler(binding.formularCustomerInc);
        tiefbauHandler = new TiefbauHandler(binding.formularTiefbauInc);
        pdfGenerator = new PDFGenerator(this, signatureHandler, headerHandler, bestandHandler, kindHandler, customerHandler, tiefbauHandler,imageHandler);
        sonstiges = binding.editTextTextMultiLine;
    }


    public void createSendButton() {
        binding.buttonSendPdf.setOnClickListener(v -> {
            File file = pdfGenerator.generatePDF();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{customerHandler.getEmail(),"kusel-seith@seith-energietechnik.de"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Begehungsprotokoll - Adresse:" + headerHandler.getAdress());
            i.putExtra(Intent.EXTRA_TEXT, "Unsere Aktenzeichen: \n\n Projektnummer: " + headerHandler.getProject() +
                    "\n Auftragsnr: " + headerHandler.getOrder() + "\n\n es betreute Sie " + headerHandler.getPerson() + "\n\n E-Mail Eigentümer: " + customerHandler.getEmail());

            Intent chooser = Intent.createChooser(i, "Share File");
            List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);



            try {
                if (file != null && (!file.exists() || !file.canRead())) {
                    Toast.makeText(FormularActivity.this, "Die PDF konnte nicht an die Email angehängt werden.", Toast.LENGTH_SHORT).show();
                }


                Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
                i.putExtra(Intent.EXTRA_STREAM, uri);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivity(Intent.createChooser(i, "Email senden..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(FormularActivity.this, "Es ist kein geeignetes E-Mail-Programm hinterlegt!", Toast.LENGTH_SHORT).show();
            }
        });
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
        int permission3 = ContextCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS_STORAGE[2]);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED && permission3 == PackageManager.PERMISSION_GRANTED;
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