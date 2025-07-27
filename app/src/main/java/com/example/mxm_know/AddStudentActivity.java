package com.example.mxm_know;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;

public class AddStudentActivity extends ComponentActivity {
    private long batchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        setTitle("Add New Student");

        batchId = getIntent().getLongExtra("BATCH_ID", -1);
        EditText etName = findViewById(R.id.etStudentName);
        Button btnAdd = findViewById(R.id.btnAddStudent);

        if (batchId == -1) {
            Toast.makeText(this, "Invalid batch selection", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();

            if (!name.isEmpty()) {
                addStudent(name);
            } else {
                Toast.makeText(this, "Please enter student name", Toast.LENGTH_SHORT).show();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToStudentManagement();
            }
        });
    }

    private void addStudent(String name) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean success = dbHelper.addStudent(name, batchId);

        if (success) {
            Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
            navigateToStudentManagement();
        } else {
            Toast.makeText(this, "Failed to add student", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToStudentManagement() {
        Intent intent = new Intent(this, StudentManagementActivity.class);

        intent.putExtra("BATCH_ID", batchId);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);

        finish();
    }
}