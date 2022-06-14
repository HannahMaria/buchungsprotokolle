package com.example.formulare.formular_handler;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularCustomerBinding;

public class CustomerHandler {
    private final EditText owner_EDT;
    private final EditText emailInput;
    private final EditText dateInput;
    private final RadioButton yesDateRadio;
    private final RadioButton noDateRadio;

    public CustomerHandler(@NonNull FormularCustomerBinding formularCustomerBinding) {
        owner_EDT = formularCustomerBinding.ownerInput;
        emailInput = formularCustomerBinding.emailInput;
        dateInput = formularCustomerBinding.dateInput;
        yesDateRadio = formularCustomerBinding.yesDateRadio;
        noDateRadio = formularCustomerBinding.noDateRadio;
        setVisible(View.GONE);

        yesDateRadio.setOnClickListener(v -> {
            if (yesDateRadio.isChecked()) {
                setVisible(View.VISIBLE);
            } else {
                setVisible(View.GONE);
            }
        });
        noDateRadio.setOnClickListener(v -> {
            if (yesDateRadio.isChecked()) {
                setVisible(View.VISIBLE);
            } else {
                setVisible(View.GONE);
            }
        });
    }

    private void setVisible(int visible) {
        dateInput.setVisibility(visible);
    }

    public String getOwner() {
        return owner_EDT.getText().toString();
    }

    public String getDateInput() {
        return dateInput.getText().toString();
    }

    public String getEmail() {
        return emailInput.getText().toString();
    }

    public boolean getYesDate() {
        return yesDateRadio.isChecked();
    }

    public boolean getNoDate() {
        return noDateRadio.isChecked();
    }
}
