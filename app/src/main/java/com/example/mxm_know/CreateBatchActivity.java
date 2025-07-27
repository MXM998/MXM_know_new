package com.example.mxm_know;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;


public class CreateBatchActivity extends ComponentActivity {
    private EditText etBatchName, etBatchNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_batch);
        setTitle("Create New Batch");


        etBatchName = findViewById(R.id.etBatchName);
        etBatchNumber = findViewById(R.id.etBatchNumber);
        Button btnCreate = findViewById(R.id.btnCreateBatch);

        btnCreate.setOnClickListener(v -> createBatch());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToBatchManagement();
            }
        });
    }

    private void createBatch() {
        String name = etBatchName.getText().toString().trim();
        String number = etBatchNumber.getText().toString().trim();

        if (name.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean success = dbHelper.addBatch(name, number);

        if (success) {
            Toast.makeText(this, "Batch created successfully", Toast.LENGTH_SHORT).show();
            navigateToBatchManagement();
        } else {
            Toast.makeText(this, "Failed to create batch", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToBatchManagement() {
        Intent intent = new Intent(CreateBatchActivity.this, BatchManagementActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}