package com.software.androidthesis.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.software.androidThesis.R;

public class UserEditActivity extends AppCompatActivity {
    private Button InButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        InButton = findViewById(R.id.In_btn);


        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("选择日期");


        InButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserEditActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}