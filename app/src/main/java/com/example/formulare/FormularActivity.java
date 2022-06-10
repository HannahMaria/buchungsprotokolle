package com.example.formulare;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.formulare.databinding.ActivityFormularBinding;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FormularActivity extends AppCompatActivity {
    private ActivityFormularBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    // ImageView

    ImageView IVPreviewImage;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;

    //Signature Pad
    private SignaturePad mSignaturePad_Begeher;
    private SignaturePad mSignaturePad_Kunde;
    private Button mClearButton_Begeher;
    private Button mSaveButton_Begeher;
    private Button mClearButton_Kunde;
    private Button mSaveButton_Kunde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formular);
        binding = ActivityFormularBinding.inflate(getLayoutInflater());
//        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());

        //TODO: Johannes Peter - Image Uplaod -- wenn das nicht läuft sollen sie es im zweifel einfach an ihre Email anhängen....
        //aktuell kann ich zwar bilder auswählen aber dann stürzt es ab... und geht wieder in die MainActivity...
        IVPreviewImage = findViewById(R.id.image_view);
        binding.formularImagesInc.butonImageSelect.setOnClickListener(v -> {
            System.out.println("Clicked userbutton");

            imageChooser();
        });

        createSignaturePads();
        createPDFButton();

    }

    //Signature Pads
    public void createSignaturePads() {
        mSignaturePad_Begeher = findViewById(R.id.signature_pad_begeher);
        mSignaturePad_Kunde = findViewById(R.id.signature_pad_kunde);
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

        mClearButton_Begeher = findViewById(R.id.clear_button_begeher);
        mSaveButton_Begeher = findViewById(R.id.save_button_begeher);
        mClearButton_Kunde = findViewById(R.id.clear_button_kunde);
        mSaveButton_Kunde = findViewById(R.id.save_button_kunde);

        mClearButton_Begeher.setOnClickListener(view -> mSignaturePad_Begeher.clear());
        mClearButton_Kunde.setOnClickListener(view -> mSignaturePad_Kunde.clear());

        mSaveButton_Begeher.setOnClickListener(view -> {
            Bitmap signatureBitmap = mSignaturePad_Begeher.getSignatureBitmap();
            if (addJpgSignatureToGallery(signatureBitmap, "Begeher")) {
                Toast.makeText(FormularActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FormularActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
            }
            if (addSvgSignatureToGallery(mSignaturePad_Begeher.getSignatureSvg())) {
                Toast.makeText(FormularActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FormularActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
            }
        });
        mSaveButton_Kunde.setOnClickListener(view -> {
            Bitmap signatureBitmap = mSignaturePad_Kunde.getSignatureBitmap();
            if (addJpgSignatureToGallery(signatureBitmap, "Kunde")) {
                Toast.makeText(FormularActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FormularActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
            }
            if (addSvgSignatureToGallery(mSignaturePad_Kunde.getSignatureSvg())) {
                Toast.makeText(FormularActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FormularActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Image Views
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature, String who) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format(who + "Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        FormularActivity.this.sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
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


    private void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(intent);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                //TODO: Im Debugging komme ich hier nicht an
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        binding.formularImagesInc.imageView.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        //TODO: List of Images???
                        IVPreviewImage.setImageURI(mArrayUri.get(1));

                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //GERNERATE PDF

    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp;

    // constant code for runtime permissions


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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FormularActivity.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}