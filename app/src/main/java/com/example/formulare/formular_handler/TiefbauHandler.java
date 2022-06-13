package com.example.formulare.formular_handler;

import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularTiefbauBinding;

public class TiefbauHandler {

    private final RadioButton necessary_RB;
    private final RadioButton notNecessary_RB;
    private final EditText overground_EDT;
    private final EditText length_EDT;

    public TiefbauHandler(@NonNull FormularTiefbauBinding formularTiefbauBinding) {
        necessary_RB = formularTiefbauBinding.yesTiefbauRadio;
        notNecessary_RB = formularTiefbauBinding.noTiefbauRadio;
        overground_EDT = formularTiefbauBinding.groundInput;
        length_EDT = formularTiefbauBinding.lengthInput;
    }

    public boolean getNecessary_RB() {
        return necessary_RB.isChecked();
    }

    public boolean getNotNecessary_RB() {
        return notNecessary_RB.isChecked();
    }

    public String getOverground_EDT() {
        return overground_EDT.getText().toString();
    }

    public String getLength_EDT() {
        return length_EDT.getText().toString();
    }
}
