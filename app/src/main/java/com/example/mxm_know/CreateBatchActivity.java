package com.example.mxm_know;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.ComponentActivity;


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
    }

    private void createBatch() {
        String name = etBatchName.getText().toString();
        String number = etBatchNumber.getText().toString();

        if (!name.isEmpty() && !number.isEmpty()) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.addBatch(name, number);
            BackBatchMangment();
        }
    }

    private void BackBatchMangment()
    {
        Intent intent = new Intent(this, BatchManagementActivity.class);
        startActivity(intent);
    }
}