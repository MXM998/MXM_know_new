package com.example.mxm_know;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.ComponentActivity;


public class AddStudentActivity extends ComponentActivity {
    private long batchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        setTitle("Add New Student");

        batchId = getIntent().getLongExtra("BATCH_ID", -1);
        EditText etName = findViewById(R.id.etStudentName);
        EditText etPhone = findViewById(R.id.etPhone);
        Button btnAdd = findViewById(R.id.btnAddStudent);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();

            if (!name.isEmpty() && !phone.isEmpty()) {
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                dbHelper.addStudent(name, phone, batchId);
                BackToStuPage();
            }
        });
    }
    private void BackToStuPage()
    {
        Intent intent = new Intent(this, StudentManagementActivity.class);
        intent.putExtra("BATCH_ID", batchId);
        startActivity(intent);
    }
}