package com.example.mxm_know;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.core.content.ContextCompat;

public class BatchManagementActivity extends ComponentActivity {
    private DatabaseHelper dbHelper;
    private LinearLayout batchesContainer;

    @Override
    protected void onResume() {
        super.onResume();
        loadBatches();
    }

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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16); // هامش سفلي 16dp

        if (cursor.getCount() == 0) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No batches available. Create your first batch.");
            tvEmpty.setTextSize(18);
            tvEmpty.setTextColor(Color.parseColor("#7B1FA2")); // بنفسجي غامق
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setPadding(0, 32, 0, 32);
            batchesContainer.addView(tvEmpty);
        } else {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") long batchId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_BATCH_ID));
                @SuppressLint("Range") String batchName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BATCH_NAME));

                Button batchButton = new Button(this);
                batchButton.setLayoutParams(params);
                batchButton.setText(batchId +"-  "+ batchName);
                batchButton.setTextSize(16);
                batchButton.setAllCaps(false);
                batchButton.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                batchButton.setPadding(32, 16, 16, 16);
                batchButton.setTextColor(Color.parseColor("#FFFFFF")); // بنفسجي داكن للنص

                Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_group);
                if (icon != null) {
                    icon.setBounds(0, 0, 32, 32);
                    batchButton.setCompoundDrawables(icon, null, null, null);
                    batchButton.setCompoundDrawablePadding(16);
                }

                batchButton.setBackgroundResource(R.drawable.btn_batc_manger);

                batchButton.setOnClickListener(v -> openBatch(batchId , batchName));
                batchesContainer.addView(batchButton);
            }
        }
        cursor.close();
    }

    private void openBatch(long batchId , String name_bt) {
        Intent intent = new Intent(this, StudentManagementActivity.class);
        intent.putExtra("BATCH_ID", batchId);
        intent.putExtra("Batch_name", name_bt);
        startActivity(intent);
    }
}