package com.example.formulare.formular_handler;

import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularBestandBinding;

public class BestandHandler {
    private final RadioButton yesCable_RB;
    private final RadioButton noCable_RB;
    private final RadioButton yesIn_RB;
    private final RadioButton noIn_RB;

    public BestandHandler(@NonNull FormularBestandBinding formularBestandBinding) {
        yesCable_RB = formularBestandBinding.yesCableRadio;
        noCable_RB = formularBestandBinding.noCableRadio;
        yesIn_RB = formularBestandBinding.yesInRadio;
        noIn_RB = formularBestandBinding.noInRadio;
    }

    public boolean yesCable() {
        return yesCable_RB.isChecked();
    }

    public boolean noCable() {
        return noCable_RB.isChecked();
    }

    public boolean yesIn() {
        return yesIn_RB.isChecked();
    }

    public boolean noIn() {
        return noIn_RB.isChecked();
    }

}
