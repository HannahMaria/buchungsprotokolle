package com.example.formulare.formular_handler;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.formulare.FormularActivity;
import com.example.formulare.databinding.FormularImagesBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;

public class ImageHandler {

    private final FormularActivity formularActivity;
   // private final ActivityResultLauncher<Intent> activityResultLauncher;
    private final ImageView imageView;
    private final Button selectImage_BTN;
    private final int PICK_IMAGE_MULTIPLE = 1;
    private final int REQUEST_PHOTO_PICKER_SINGLE_SELECT = 2;
    private final int REQUEST_PHOTO_PICKER_MULTI_SELECT = 3;

    public ImageHandler(@NonNull FormularActivity formularActivity, @NonNull FormularImagesBinding formularImagesBinding) {
        this.formularActivity = formularActivity;
        imageView = formularImagesBinding.imageView;
        selectImage_BTN = formularImagesBinding.butonImageSelect;
      /*  activityResultLauncher = formularActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                callback -> onActivityResult(1, callback.getResultCode(), callback.getData())
        );*/
        //TODO: Johannes Peter - Image Uplaod -- wenn das nicht läuft sollen sie es im zweifel einfach an ihre Email anhängen....
       // selectImage_BTN.setOnClickListener(view -> activityResultLauncher.launch(imageChooser()));
    }

    private Intent imageChooser() {
        Intent intent = new Intent();//MediaS*tore.ACTION_PICK_IMAGES
        intent.setType("image/*");
       // activityResultLauncher.launch(intent);
        return intent;
    }

    protected void onActivityResult(
            int requestCode, int resultCode, final Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            // Handle error
            return;
        }
        Uri currentUri;

        switch (requestCode) {
            case REQUEST_PHOTO_PICKER_SINGLE_SELECT:
                // Get photo picker response for single select.
                currentUri = data.getData();
                imageView.setImageURI(currentUri);
                // Do stuff with the photo/video URI.
                return;
            case REQUEST_PHOTO_PICKER_MULTI_SELECT:
                // Get photo picker response for multi select
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    currentUri = data.getClipData().getItemAt(i).getUri();

                    imageView.setImageURI(currentUri);
                    return;
                }
        }
    }

    public void onActivityResults(int resultCode, @Nullable Intent data) {
        if (data != null) {
            try {
                Uri content_describer = data.getData();
                String fileMimeType_String = getFileMimeTypeFromUri_String(content_describer);
                File temp_File = createTempFile(fileMimeType_String);
                try (InputStream in = formularActivity.getContentResolver().openInputStream(content_describer); OutputStream out = new FileOutputStream(temp_File)) {
                    // open the user-picked file for reading:
                    // open the output-file:
                    // copy the content:
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    // Contents are copied!
                }
                Bitmap bitmap = BitmapFactory.decodeFile(temp_File.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(formularActivity, "Ausgewähltes Dateiformat wird nicht unterstützt!", Toast.LENGTH_LONG).show();
            }

            //alternative
//            if (result.getResultCode()
//                    == Activity.RESULT_OK) {
//                Intent data = result.getData();
//                // do your operation from here....
//                if (data != null
//                        && data.getData() != null) {
//                    Uri selectedImageUri = data.getData();
//                    Bitmap selectedImageBitmap = null;
//                    try {
//                        selectedImageBitmap
//                                = MediaStore.Images.Media.getBitmap(
//                                this.getContentResolver(),
//                                selectedImageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    imageHandler.getImageView().setImageBitmap(selectedImageBitmap);
//                }
//            }
        }
    }

    public File createTempFile(@NonNull String imageMimeType_String) throws IOException {
        // Create an image file name
        String timeStamp = DateFormat.getDateInstance().format(System.currentTimeMillis());
        String imageFileName = imageMimeType_String.replace(".", "").toUpperCase() + "_" + timeStamp + "_TEMP";
        File storageDir_File = formularActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName,  /* prefix */
                imageMimeType_String,         /* suffix */
                storageDir_File      /* directory */
        );
    }

    private String getFileMimeTypeFromUri_String(@NonNull Uri uri) {
        String fileMimeType_String;
        ContentResolver contentResolver = formularActivity.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        fileMimeType_String = mime.getExtensionFromMimeType(contentResolver.getType(uri));
        return "." + fileMimeType_String;
    }

    @NonNull
    public ImageView getImageView() {
        return imageView;
    }

    public int getPickImageMultiple() {
        return PICK_IMAGE_MULTIPLE;
    }
}
