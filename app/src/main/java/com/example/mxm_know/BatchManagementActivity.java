package com.example.mxm_know;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;

public class BatchManagementActivity extends ComponentActivity {
    private DatabaseHelper dbHelper;
    private LinearLayout batchesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_management);
        setTitle("Knowledge");

        dbHelper = new DatabaseHelper(this);
        batchesContainer = findViewById(R.id.batchesContainer);
        Button btnCreateBatch = findViewById(R.id.btnCreateBatch);

        btnCreateBatch.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateBatchActivity.class);
            startActivity(intent);
        });

        loadBatches();
    }

    private void loadBatches() {
        batchesContainer.removeAllViews();
        Cursor cursor = dbHelper.getAllBatches();

        if (cursor.getCount() == 0) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No batches available. Create your first batch.");
            batchesContainer.addView(tvEmpty);
        } else {
            while (cursor.moveToNext()) {
                long batchId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_BATCH_ID));
                String batchName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BATCH_NAME));

                Button batchButton = new Button(this);
                batchButton.setText(batchName);
                batchButton.setOnClickListener(v -> openBatch(batchId));
                batchesContainer.addView(batchButton);
            }
        }
        cursor.close();
    }

    private void openBatch(long batchId) {
        Intent intent = new Intent(this, StudentManagementActivity.class);
        intent.putExtra("BATCH_ID", batchId);
        startActivity(intent);
    }
}