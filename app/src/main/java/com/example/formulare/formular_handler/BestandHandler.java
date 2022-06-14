package com.example.formulare.formular_handler;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularBestandBinding;

public class BestandHandler {
    private final RadioButton yesCable;
    private final RadioButton noCable;
    private final RadioButton yesIn;
    private final RadioButton noIn;
    private final EditText cableInput;


    public BestandHandler(@NonNull FormularBestandBinding formularBestandBinding) {
        yesCable = formularBestandBinding.yesCableRadio;
        noCable = formularBestandBinding.noCableRadio;
        yesIn = formularBestandBinding.yesInRadio;
        noIn = formularBestandBinding.noInRadio;
        cableInput = formularBestandBinding.cableInput;
        setVisible(View.GONE);
        yesCable.setOnClickListener(v -> {
            if (yesCable.isChecked()) {
                setVisible(View.VISIBLE);
            } else {
                setVisible(View.GONE);
            }
        });
        noCable.setOnClickListener(v -> {
            if (yesCable.isChecked()) {
                setVisible(View.VISIBLE);
            } else {
                setVisible(View.GONE);
            }
        });
    }

    private void setVisible(int visible) {
        cableInput.setVisibility(visible);
    }

    public boolean yesCable() {
        return yesCable.isChecked();
    }

    public boolean noCable() {
        return noCable.isChecked();
    }

    public boolean yesIn() {
        return yesIn.isChecked();
    }

    public boolean noIn() {
        return noIn.isChecked();
    }

    public String getCableInput() {
        return cableInput.getText().toString();
    }

}
