package com.example.formulare.formular_handler;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularTiefbauBinding;

public class TiefbauHandler {

    private final RadioButton necessary;
    private final RadioButton notNecessary;
    private final CheckBox withoutOber;
    private final EditText withoutOberLength;
    private final CheckBox pflasterOber;
    private final EditText pflasterOberLength;
    private final CheckBox asphaltOber;
    private final EditText asphaltOberLength;
    private final LinearLayout tiefbauLayout;

    public TiefbauHandler(@NonNull FormularTiefbauBinding formularTiefbauBinding) {
        necessary = formularTiefbauBinding.yesTiefbauRadio;
        notNecessary = formularTiefbauBinding.noTiefbauRadio;
        withoutOber = formularTiefbauBinding.without;
        withoutOberLength = formularTiefbauBinding.lengthWithoutInput;
        pflasterOber = formularTiefbauBinding.pflaster;
        pflasterOberLength = formularTiefbauBinding.lengthPlasterInput;
        asphaltOber = formularTiefbauBinding.asphalt;
        asphaltOberLength = formularTiefbauBinding.lengthAsphaltInput;
        tiefbauLayout = formularTiefbauBinding.groundLayout;

        tiefbauLayout.setVisibility(View.GONE);
        formularTiefbauBinding.withoutLayout2.setVisibility(View.GONE);
        formularTiefbauBinding.pflasterLayout1.setVisibility(View.GONE);
        formularTiefbauBinding.asphaltLayout2.setVisibility(View.GONE);

        necessary.setOnClickListener(v -> {
            if (necessary.isChecked()) {
                tiefbauLayout.setVisibility(View.VISIBLE);
            } else {
                tiefbauLayout.setVisibility(View.GONE);
            }
        });
        notNecessary.setOnClickListener(v -> {
            if (necessary.isChecked()) {
                tiefbauLayout.setVisibility(View.VISIBLE);
            } else {
                tiefbauLayout.setVisibility(View.GONE);
            }
        });

        asphaltOber.setOnClickListener(v -> {
            if (asphaltOber.isChecked()) {
                formularTiefbauBinding.asphaltLayout2.setVisibility(View.VISIBLE);
            } else {
                formularTiefbauBinding.asphaltLayout2.setVisibility(View.GONE);
            }
        });
        pflasterOber.setOnClickListener(v -> {
            if (pflasterOber.isChecked()) {
                formularTiefbauBinding.pflasterLayout1.setVisibility(View.VISIBLE);
            } else {
                formularTiefbauBinding.pflasterLayout1.setVisibility(View.GONE);
            }
        });
        withoutOber.setOnClickListener(v -> {
            if (withoutOber.isChecked()) {
                formularTiefbauBinding.withoutLayout2.setVisibility(View.VISIBLE);
            } else {
                formularTiefbauBinding.withoutLayout2.setVisibility(View.GONE);
            }
        });
    }


    public boolean getNecessary() {
        return necessary.isChecked();
    }

    public boolean getNotNecessary() {
        return notNecessary.isChecked();
    }

    public boolean getWithoutOber() {
        return withoutOber.isChecked();
    }

    public String getWithoutOberLength() {
        return withoutOberLength.getText().toString();
    }

    public boolean getPflasterOber() {
        return pflasterOber.isChecked();
    }

    public String getPflasterOberLength() {
        return pflasterOberLength.getText().toString();
    }

    public boolean getAsphaltOber() {
        return asphaltOber.isChecked();
    }

    public String getAsphaltOberLength() {
        return asphaltOberLength.getText().toString();
    }
}
