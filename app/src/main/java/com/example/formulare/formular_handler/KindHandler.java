package com.example.formulare.formular_handler;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularKindBinding;

public class KindHandler {

    private final RadioButton company;
    private final RadioButton privat;
    private final RadioButton oneFamilyHouse;
    private final RadioButton moreFamilyHouse;
    private final TextView haus_art;

    public KindHandler(@NonNull FormularKindBinding formularKindBinding) {
        company = formularKindBinding.companyRadio;
        privat = formularKindBinding.privatRadio;
        oneFamilyHouse = formularKindBinding.oneFamilyRadio;
        moreFamilyHouse = formularKindBinding.moreFamilyRadio;
        haus_art = formularKindBinding.textViewKind;
        setVisible(View.GONE);

        privat.setOnClickListener(v -> {
            if (privat.isChecked()) {
                setVisible(View.VISIBLE);
            } else {
                setVisible(View.GONE);
            }
        });
        company.setOnClickListener(v -> {
            if (privat.isChecked()) {
                setVisible(View.VISIBLE);
            } else {
                setVisible(View.GONE);
            }
        });
    }

    private void setVisible(int visible) {
        haus_art.setVisibility(visible);
        oneFamilyHouse.setVisibility(visible);
        moreFamilyHouse.setVisibility(visible);
    }

    public boolean companyManner() {
        return company.isChecked();
    }

    public boolean privateManner() {
        return privat.isChecked();
    }

    public boolean onFamilyHouse() {
        return oneFamilyHouse.isChecked();
    }

    public boolean moreFamilyHouse() {
        return moreFamilyHouse.isChecked();
    }
}
