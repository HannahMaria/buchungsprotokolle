package com.example.formulare.formular_handler;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularBestandBinding;

public class BestandHandler {
    private final RadioButton yesCable;
    private final RadioButton noCable;
    private final RadioButton yesIn;
    private final RadioButton yesInComplete;
    private final RadioButton noIn;
    private final RadioButton noInComplete;
    private final EditText cableInput;
    private final LinearLayout inLayoutComplete;


    public BestandHandler(@NonNull FormularBestandBinding formularBestandBinding) {
        yesCable = formularBestandBinding.yesCableRadio;
        noCable = formularBestandBinding.noCableRadio;
        yesIn = formularBestandBinding.yesInRadio;
        noIn = formularBestandBinding.noInRadio;
        yesInComplete = formularBestandBinding.yesInRadioComplete;
        noInComplete = formularBestandBinding.noInRadioComplete;
        cableInput = formularBestandBinding.cableInput;
        inLayoutComplete = formularBestandBinding.inLayoutComplete;
        inLayoutComplete.setVisibility(View.GONE);
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
        yesIn.setOnClickListener(v -> {
            if (yesIn.isChecked()) {
                inLayoutComplete.setVisibility(View.VISIBLE);
            } else {
                inLayoutComplete.setVisibility(View.GONE);
            }
        });
        noIn.setOnClickListener(v -> {
            if (yesIn.isChecked()) {
                inLayoutComplete.setVisibility(View.VISIBLE);
            } else {
                inLayoutComplete.setVisibility(View.GONE);
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

    public boolean getYesInComplete() {
        return yesInComplete.isChecked();
    }

    public boolean getNoInComplete() {
        return noInComplete.isChecked();
    }
}
