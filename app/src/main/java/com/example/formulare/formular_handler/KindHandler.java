package com.example.formulare.formular_handler;

import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularKindBinding;

public class KindHandler {

    private final RadioButton company_RB;
    private final RadioButton private_RB;
    private final RadioButton onFamilyHouse_RB;
    private final RadioButton moreFamilyHouse_RB;

    public KindHandler(@NonNull FormularKindBinding formularKindBinding) {
        company_RB = formularKindBinding.companyRadio;
        private_RB = formularKindBinding.privatRadio;
        onFamilyHouse_RB = formularKindBinding.oneFamilyRadio;
        moreFamilyHouse_RB = formularKindBinding.moreFamilyRadio;
    }

    public boolean companyManner() {
        return company_RB.isChecked();
    }

    public boolean privateManner() {
        return private_RB.isChecked();
    }

    public boolean onFamilyHouse() {
        return onFamilyHouse_RB.isChecked();
    }

    public boolean moreFamilyHouse() {
        return moreFamilyHouse_RB.isChecked();
    }
}
