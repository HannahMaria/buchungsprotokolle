package com.example.formulare.formular_handler;

import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularHeaderBinding;

public class HeaderHandler {
    private final EditText project;
    private final EditText order;
    private final EditText person;
    private final EditText adress;

    public HeaderHandler(@NonNull FormularHeaderBinding formularHeaderBinding) {
        project = formularHeaderBinding.projectInput;
        order = formularHeaderBinding.orderInput;
        person = formularHeaderBinding.personInput;
        adress = formularHeaderBinding.adressInput;
    }

    public String getProject() {
        return project.getText().toString();
    }

    public String getOrder() {
        return order.getText().toString();
    }

    public String getPerson() {
        return person.getText().toString();
    }

    public String getAdress() {
        return adress.getText().toString();
    }

}
