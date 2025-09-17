package com.example.mxm_know;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;



public class StudentManagementActivity extends ComponentActivity {
    private long batchId;
    private String Batch_name_e;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);
        setTitle("Knowledge");

        batchId = getIntent().getLongExtra("BATCH_ID", -1);
        Batch_name_e = getIntent().getStringExtra("Batch_name");
        dbHelper = new DatabaseHelper(this);

        TextView st_mnn = findViewById(R.id.St_man);
        st_mnn.setText(Batch_name_e);
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
        loadStudents();
    }

    private void loadStudents() {
        LinearLayout studentsContainer = findViewById(R.id.studentsContainer);
        studentsContainer.removeAllViews();
        Cursor cursor = dbHelper.getStudentsByBatch(batchId);



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);

        if (cursor.getCount() == 0) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No students in this batch");
            tvEmpty.setTextSize(18);
            tvEmpty.setTextColor(Color.parseColor("#7B1FA2"));
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setPadding(0, 32, 0, 32);

            studentsContainer.addView(tvEmpty);
        }
        else
        {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") long studentId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
                @SuppressLint("Range") int points = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POINTS));
                @SuppressLint("Range") int is_done = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_DONE));
                @SuppressLint("Range") int comb_7 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMBO_7));

                Button studentButton = new Button(this);
                studentButton.setLayoutParams(params);
                if (is_done == 0 && comb_7 >= 3)
                {
                    studentButton.setText(name + "  Points: " + points + "  Combo  x" + comb_7 );
                }
                else if (is_done == 0)
                {
                    studentButton.setText(name + "  Points: " + points );
                }
                else
                {
                    studentButton.setText(name + "  Points: " + points + "     (-" + is_done+")");
                }
                studentButton.setTextSize(16);
                studentButton.setAllCaps(false);
                studentButton.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                studentButton.setPadding(32, 16, 16, 16);
                studentButton.setTextColor(Color.parseColor("#FFFFFF"));


                studentButton.setBackgroundResource(R.drawable.button_gradient_purple_pink);

                setColorCombo(studentButton , comb_7);

                if (is_done == 1) {
                    studentButton.setBackgroundResource(R.drawable.buuton_not_doing);
                }
                if (is_done == 2)
                {
                    studentButton.setBackgroundResource(R.drawable.button_not_doing_2);
                }
                if (is_done >= 3)
                {
                    studentButton.setBackgroundResource(R.drawable.button_not_doing_3);
                }

                studentButton.setOnClickListener(v -> showPointsDialog(studentId, name));
                studentsContainer.addView(studentButton);

                studentButton.setOnLongClickListener(v -> {
                    showEditDeleteDialog(studentId, name);
                    return true;
                });
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
        Button btn_good_work_6 = dialogView.findViewById(R.id.btn_good_work);
        Button btnNormal = dialogView.findViewById(R.id.btnNormal);
        Button btnNotDone = dialogView.findViewById(R.id.btnNotDone);
        Button btnAI = dialogView.findViewById(R.id.btnAI);

        tvTitle.setText("Add points for " + studentName);

        applyShineAnimation(btnExcellent);

        AlertDialog dialog = builder.create();


        btnExcellent.setOnClickListener(v -> {
            updatePoints(studentId, 7);
            dialog.dismiss();
        });
        btn_good_work_6.setOnClickListener(v -> {
            updatePoints(studentId, 6);
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
    private void applyShineAnimation(Button button) {

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(
                button,
                "alpha",
                1.0f,
                0.6f,
                1.0f
        );
        alphaAnim.setDuration(1500);
        alphaAnim.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnim.setRepeatMode(ObjectAnimator.REVERSE);

        alphaAnim.start();

    }

    private  void setColorCombo(Button buttonsent , int Comob_strisk)
    {
        if (Comob_strisk == 1)
        {
            applyColorAnimation(buttonsent, Combo_color.Blue);
        }
        else if (Comob_strisk == 2)
        {
            applyColorAnimation(buttonsent, Combo_color.Blue_green);
        }
        else if (Comob_strisk == 3)
        {
            applyColorAnimation(buttonsent, Combo_color.Gold);
        }
        else if (Comob_strisk == 4)
        {
            applyColorAnimation(buttonsent, Combo_color.Red);
        }
        else if (Comob_strisk == 5)
        {
            applyColorAnimation(buttonsent, Combo_color.Red_Gold);
        }
        else if (Comob_strisk >= 6)
        {
            applyColorAnimation(buttonsent , Combo_color.Galactic_Purple);
            applyShineAnimation(buttonsent);
        }
        else
        {
            return;
        };
    }
    private void applyColorAnimation(Button buttonsent, Combo_color colorType) {
        GradientDrawable originalDrawable = (GradientDrawable) buttonsent.getBackground();
        GradientDrawable animatedDrawable = (GradientDrawable) originalDrawable.mutate();
        buttonsent.setBackground(animatedDrawable);

        ValueAnimator colorAnim = null;

        switch (colorType) {
            case Blue:
                colorAnim = ValueAnimator.ofArgb(
                        Color.parseColor("#00FF00"),
                        Color.parseColor("#228B22"),
                        Color.parseColor("#FFFFFF"),
                        Color.parseColor("#228B22"),
                        Color.parseColor("#00FF00")
                );
                break;

            case Blue_green:
                colorAnim = ValueAnimator.ofArgb(
                        Color.parseColor("#191970"),
                        Color.parseColor("#32CD32"),
                        Color.parseColor("#00BFFF"),
                        Color.parseColor("#00FA9A"),
                        Color.parseColor("#191970")
                );
                break;
            case Gold:
                colorAnim = ValueAnimator.ofArgb(
                        Color.parseColor("#FFD700"),
                        Color.parseColor("#FFEC8B"),
                        Color.parseColor("#FFFFFF"),
                        Color.parseColor("#FFEC8B"),
                        Color.parseColor("#FFD700")
                );
                break;
            case Red:
                colorAnim = ValueAnimator.ofArgb(
                        Color.parseColor("#FF0000"),
                        Color.parseColor("#DC143C"),
                        Color.parseColor("#FFFFFF"),
                        Color.parseColor("#B22222"),
                        Color.parseColor("#FF0000")
                );
                break;

            case Red_Gold:
                colorAnim = ValueAnimator.ofArgb(
                        Color.parseColor("#FF0000"),
                        Color.parseColor("#FFD700"), // Gold
                        Color.parseColor("#FF4500"), // OrangeRed
                        Color.parseColor("#FF8C00"), // DarkOrange
                        Color.parseColor("#FFD700"), // Gold
                        Color.parseColor("#FFA500"), // Orange
                        Color.parseColor("#FF0000")  // Red
                );
                break;

            case Galactic_Purple:
                colorAnim = ValueAnimator.ofArgb(
                        Color.parseColor("#8A2BE2"),
                        Color.parseColor("#9370DB"),
                        Color.parseColor("#4B0082"),
                        Color.parseColor("#9400D3"),
                        Color.parseColor("#DA70D6"),
                        Color.parseColor("#EE82EE"),
                        Color.parseColor("#8A2BE2")
                );
                break;


        }

        if (colorAnim != null) {
            colorAnim.addUpdateListener(animator -> {
                animatedDrawable.setStroke(7, (Integer) animator.getAnimatedValue());
            });


            colorAnim.setDuration(1500);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.start();
        }
    }
    public enum Combo_color {
        Blue,
        Blue_green,
        Gold,
        Red,
        Red_Gold,
        Galactic_Purple,
    }
    private void showEditDeleteDialog(long studentId, String studentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_student_options, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        tvTitle.setText("Options for " + studentName);

        AlertDialog dialog = builder.create();

        btnEdit.setOnClickListener(v -> {
            dialog.dismiss();
            showEditStudentDialog(studentId, studentName);
        });

        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirmationDialog(studentId, studentName);
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    private void showEditStudentDialog(long studentId, String currentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Student");

        final EditText input = new EditText(this);
        input.setText(currentName);
        input.setSelection(input.getText().length());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                dbHelper.updateStudent(studentId, newName);
                loadStudents();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteConfirmationDialog(long studentId, String studentName) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete " + studentName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteStudent(studentId);
                    loadStudents();
                })
                .setNegativeButton("No", null)
                .show();
    }
}