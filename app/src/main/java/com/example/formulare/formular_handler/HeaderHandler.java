package com.example.formulare.formular_handler;

import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.formulare.databinding.FormularHeaderBinding;

public class HeaderHandler {
    private final EditText project_EDT;
    private final EditText order_EDT;
    private final EditText owner_EDT;
    private final EditText person_EDT;
    private final EditText connection_EDT;

    public HeaderHandler(@NonNull FormularHeaderBinding formularHeaderBinding) {
        project_EDT = formularHeaderBinding.projectInput;
        order_EDT = formularHeaderBinding.orderInput;
        owner_EDT = formularHeaderBinding.ownerInput;
        person_EDT = formularHeaderBinding.personInput;
        connection_EDT = formularHeaderBinding.connectionInput;
    }

    public String getProject() {
        return project_EDT.getText().toString();
    }

    public String getOrder() {
        return order_EDT.getText().toString();
    }

    public String getOwner() {
        return owner_EDT.getText().toString();
    }

    public String getPerson() {
        return person_EDT.getText().toString();
    }

    public String getConnection() {
        return connection_EDT.getText().toString();
    }

}
