package com.example.mxm_know;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.ComponentActivity;

public class AddPointsActivity extends ComponentActivity {
    private long studentId;
    private DatabaseHelper dbHelper;

    private long batchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_points);
        setTitle("Add Points");

        batchId = getIntent().getLongExtra("BATCH_ID", -1);
        studentId = getIntent().getLongExtra("STUDENT_ID", -1);
        dbHelper = new DatabaseHelper(this);

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Add points for student");

        Button btnNormal = findViewById(R.id.btnNormal);
        Button btnExcellent = findViewById(R.id.btnExcellent);
        Button btnNotDone = findViewById(R.id.btnNotDone);

        btnNormal.setOnClickListener(v -> addPoints(5));
        btnExcellent.setOnClickListener(v -> addPoints(7));
        btnNotDone.setOnClickListener(v -> addPoints(0));
    }

    private void addPoints(int points) {
        dbHelper.updatePoints(studentId, points);
        BackToStuPage();
    }
    private void BackToStuPage()
    {
        Intent intent = new Intent(this, StudentManagementActivity.class);
        intent.putExtra("BATCH_ID", batchId);
        startActivity(intent);
    }
}