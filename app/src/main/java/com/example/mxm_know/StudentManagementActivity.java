package com.example.mxm_know;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;

public class StudentManagementActivity extends ComponentActivity {
    private long batchId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);
        setTitle("Knowledge");

        batchId = getIntent().getLongExtra("BATCH_ID", -1);
        dbHelper = new DatabaseHelper(this);

        Button btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddStudent.setOnClickListener(v -> openAddStudentScreen());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToBatchManagement();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents(); // تحديث القائمة عند العودة للنشاط
    }

    private void loadStudents() {
        LinearLayout studentsContainer = findViewById(R.id.studentsContainer);
        studentsContainer.removeAllViews();
        Cursor cursor = dbHelper.getStudentsByBatch(batchId);

        if (cursor.getCount() == 0) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No students in this batch");
            studentsContainer.addView(tvEmpty);
        } else {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") long studentId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
                @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POINTS));
                @SuppressLint("Range") int is_done = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_DONE));

                Button studentButton = new Button(this);
                studentButton.setText(name + " - Points: " + points);
                if (is_done == 1) {
                    studentButton.setBackgroundColor(Color.DKGRAY);
                }
                studentButton.setOnClickListener(v -> showPointsDialog(studentId, name));
                studentsContainer.addView(studentButton);
            }
        }
        cursor.close();
    }

    private void openAddStudentScreen() {
        Intent intent = new Intent(this, AddStudentActivity.class);
        intent.putExtra("BATCH_ID", batchId);
        startActivity(intent);
    }

    private void navigateToBatchManagement() {
        Intent intent = new Intent(this, BatchManagementActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showPointsDialog(long studentId, String studentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_add_points, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        Button btnExcellent = dialogView.findViewById(R.id.btnExcellent);
        Button btnNormal = dialogView.findViewById(R.id.btnNormal);
        Button btnNotDone = dialogView.findViewById(R.id.btnNotDone);
        Button btnAI = dialogView.findViewById(R.id.btnAI);

        tvTitle.setText("Add points for " + studentName);

        AlertDialog dialog = builder.create();

        btnExcellent.setOnClickListener(v -> {
            updatePoints(studentId, 7);
            dialog.dismiss();
        });
        btnNormal.setOnClickListener(v -> {
            updatePoints(studentId, 5);
            dialog.dismiss();
        });
        btnNotDone.setOnClickListener(v -> {
            updatePoints(studentId, 0);
            dialog.dismiss();
        });
        btnAI.setOnClickListener(v -> {
            updatePoints(studentId, -2);
            dialog.dismiss();
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void updatePoints(long studentId, int pointsToAdd) {
        dbHelper.addStudentPoints(studentId, pointsToAdd);
        loadStudents();
    }
}