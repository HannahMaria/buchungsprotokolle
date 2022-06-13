package com.example.formulare.formular_handler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.formulare.FormularActivity;
import com.example.formulare.databinding.FormularSignatureBinding;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SignatureHandler {
    private final FormularActivity formularActivity;
    private final SignaturePad mSignaturePad_Begeher;
    private final SignaturePad mSignaturePad_Kunde;
    private final Button mClearButton_Begeher;
    private final Button mSaveButton_Begeher;
    private final Button mClearButton_Kunde;
    private final Button mSaveButton_Kunde;
    public Bitmap signatureBitmapBegeher;
    public Bitmap signatureBitmapKunde;

    public SignatureHandler(@NonNull FormularActivity formularActivity, @NonNull FormularSignatureBinding formularSignatureBinding) {
        this.formularActivity = formularActivity;
        mSignaturePad_Begeher = formularSignatureBinding.signaturePadBegeher;
        mSignaturePad_Kunde = formularSignatureBinding.signaturePadKunde;
        mClearButton_Begeher = formularSignatureBinding.clearButtonBegeher;
        mSaveButton_Begeher = formularSignatureBinding.saveButtonBegeher;
        mClearButton_Kunde = formularSignatureBinding.clearButtonKunde;
        mSaveButton_Kunde = formularSignatureBinding.saveButtonKunde;
        initListeners();
    }

    public void initListeners() {
        mSignaturePad_Kunde.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                mSaveButton_Kunde.setEnabled(true);
                mClearButton_Kunde.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton_Kunde.setEnabled(false);
                mClearButton_Kunde.setEnabled(false);
            }
        });
        mSignaturePad_Begeher.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                mSaveButton_Begeher.setEnabled(true);
                mClearButton_Begeher.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton_Begeher.setEnabled(false);
                mClearButton_Begeher.setEnabled(false);
            }
        });
        mClearButton_Begeher.setOnClickListener(view -> mSignaturePad_Begeher.clear());
        mClearButton_Kunde.setOnClickListener(view -> mSignaturePad_Kunde.clear());
        mSaveButton_Begeher.setOnClickListener(view -> {
            signatureBitmapBegeher = mSignaturePad_Begeher.getSignatureBitmap();
            if (addJpgSignatureToGallery(signatureBitmapBegeher, "Begeher")) {
                Toast.makeText(formularActivity, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(formularActivity, "Unable to store the signature", Toast.LENGTH_SHORT).show();
            }
            if (addSvgSignatureToGallery(mSignaturePad_Begeher.getSignatureSvg())) {
                Toast.makeText(formularActivity, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(formularActivity, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
            }
        });
        mSaveButton_Kunde.setOnClickListener(view -> {
            signatureBitmapKunde = mSignaturePad_Kunde.getSignatureBitmap();
            if (addJpgSignatureToGallery(signatureBitmapKunde, "Kunde")) {
                Toast.makeText(formularActivity, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(formularActivity, "Unable to store the signature", Toast.LENGTH_SHORT).show();
            }
            if (addSvgSignatureToGallery(mSignaturePad_Kunde.getSignatureSvg())) {
                Toast.makeText(formularActivity, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(formularActivity, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean addJpgSignatureToGallery(Bitmap signature, String who) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir(), String.format(who + "Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private File getAlbumStorageDir() {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SignaturePad");
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    private void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        formularActivity.sendBroadcast(mediaScanIntent);
    }

    private boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir(), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return result;
    }

}
