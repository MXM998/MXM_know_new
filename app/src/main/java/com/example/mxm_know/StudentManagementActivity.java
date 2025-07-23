package com.example.mxm_know;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;


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

        loadStudents();
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
                long studentId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_NAME));
                int points = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_POINTS));

                Button studentButton = new Button(this);
                studentButton.setText(name + " - Points: " + points);
                studentButton.setOnClickListener(v -> openAddPointsScreen(studentId));
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

    private void openAddPointsScreen(long studentId) {
        Intent intent = new Intent(this, AddPointsActivity.class);
        intent.putExtra("STUDENT_ID", studentId);
        intent.putExtra("BATCH_ID", batchId);
        startActivity(intent);
    }
}