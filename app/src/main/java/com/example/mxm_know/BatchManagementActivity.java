package com.example.mxm_know;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
                @SuppressLint("Range") long Bathc_number = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_BATCH_NUMBER));

                Button batchButton = new Button(this);
                batchButton.setLayoutParams(params);
                batchButton.setText(batchId +"-  Name : "+ batchName + "   Number : " + Bathc_number);
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

                batchButton.setOnLongClickListener(v -> {
                    showBatchOptionsDialog(batchId, batchName);
                    return true;
                });

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
    private void showBatchOptionsDialog(long batchId, String batchName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_batch_options, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        Button btnDeleteBatch = dialogView.findViewById(R.id.btnDeleteBatch);

        tvTitle.setText("Options for " + batchName);

        AlertDialog dialog = builder.create();

        btnDeleteBatch.setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteBatchDialog(batchId, batchName);
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void showDeleteBatchDialog(long batchId, String batchName) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete the batch " + batchName + " and all its students?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteBatch(batchId);
                    loadBatches();
                })
                .setNegativeButton("No", null)
                .show();
    }
}