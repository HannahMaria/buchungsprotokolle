package com.example.formulare.formular_handler;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.example.formulare.FormularActivity;
import com.example.formulare.databinding.FormularImagesBinding;

import java.util.ArrayList;

public class ImageHandler {

    private final FormularActivity formularActivity;
    private final ActivityResultLauncher<Intent> activityResultLauncher;
    private final LinearLayout imageLayout;
    private final TextView imageText;
    private final ImageView image;
    private final Button selectImage_BTN, previous, next;
    private final ImageButton delete;
    private int PICK_IMAGE_MULTIPLE = 1;
    public ArrayList<Uri> mArrayUri = new ArrayList<>();
    private int position = 0;

    public ImageHandler(@NonNull FormularActivity formularActivity, @NonNull FormularImagesBinding formularImagesBinding) {
        this.formularActivity = formularActivity;
        imageLayout = formularImagesBinding.imageViewLayout;
        image = formularImagesBinding.image;
        previous = formularImagesBinding.previous;
        delete = formularImagesBinding.delete;
        imageText = formularImagesBinding.imageTextView;
        next = formularImagesBinding.next;
        selectImage_BTN = formularImagesBinding.butonImageSelect;
        imageLayout.setVisibility(View.GONE);

        activityResultLauncher = formularActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                callback -> onActivityResult(PICK_IMAGE_MULTIPLE, callback.getResultCode(), Intent.createChooser(callback.getData(), "Select Picture"))
        );

        selectImage_BTN.setOnClickListener(view -> activityResultLauncher.launch(imageChooser()));

        // click here to select next image
        next.setOnClickListener(v -> {
            if (position < mArrayUri.size() - 1) {
                // increase the position by 1
                position++;
                imageText.setText("Bild " + (position + 1) + " von " + mArrayUri.size());
                image.setImageURI(mArrayUri.get(position));
            } else {
                Toast.makeText(formularActivity, "Last Image Already Shown", Toast.LENGTH_SHORT).show();
            }
        });

        // click here to view previous image
        previous.setOnClickListener(v -> {
            if (position > 0) {
                // decrease the position by 1
                position--;
                imageText.setText("Bild " + (position + 1) + " von " + mArrayUri.size());
                image.setImageURI(mArrayUri.get(position));
            }
        });

        delete.setOnClickListener(v -> {
            mArrayUri.remove(position);
            if (mArrayUri.size() > 0) {
                imageText.setText("Bild " + (position + 1) + " von " + mArrayUri.size());
                image.setImageURI(mArrayUri.get(position));
            } else
                imageLayout.setVisibility(View.GONE);
        });
    }

    private Intent imageChooser() {
        // initialising intent
        Intent intent = new Intent();
        // setting type to select to be image
        intent.setType("image/*");
        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    mArrayUri.add(imageurl);
                }
                // setting 1st selected image into image switcher
                image.setImageURI(mArrayUri.get(0));
                position = 0;
                imageLayout.setVisibility(View.VISIBLE);
                imageText.setText("Bild " + (position + 1) + " von " + mArrayUri.size());

            } else {
                Uri imageurl = data.getData();
                mArrayUri.add(imageurl);
                image.setImageURI(mArrayUri.get(0));
                position = 0;
                imageLayout.setVisibility(View.VISIBLE);
                imageText.setText("Bild " + (position + 1) + " von " + mArrayUri.size());

            }
        } else {
            // show this if no image is selected
            Toast.makeText(formularActivity, "Es wurde kein Bild ausgewählt", Toast.LENGTH_LONG).show();
            imageLayout.setVisibility(View.GONE);
        }
    }

}
