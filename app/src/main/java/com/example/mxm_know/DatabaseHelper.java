package com.example.mxm_know;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "knowledge.db";
    private static final int DATABASE_VERSION = 2;

    // جداول الدفعات
    public static final String TABLE_BATCHES = "batches";
    public static final String COLUMN_BATCH_ID = "batch_id";
    public static final String COLUMN_BATCH_NAME = "batch_name";
    public static final String COLUMN_BATCH_NUMBER = "batch_number";

    // جداول الطلاب
    public static final String TABLE_STUDENTS = "students";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_STUDENT_NAME = "student_name";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_BATCH_ID_FK = "batch_id";
    public static final String COLUMN_IS_DONE = "is_done";
    public  static  final String COLUMN_COMBO_7 = "Combo_7";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBatchesTable = "CREATE TABLE " + TABLE_BATCHES + "("
                + COLUMN_BATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BATCH_NAME + " TEXT, "
                + COLUMN_BATCH_NUMBER + " TEXT)";
        db.execSQL(createBatchesTable);

        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + "("
                + COLUMN_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STUDENT_NAME + " TEXT, "
                + COLUMN_POINTS + " INTEGER DEFAULT 0, "
                + COLUMN_IS_DONE + " INTEGER DEFAULT 0, "
                + COLUMN_COMBO_7 + " INTEGER DEFAULT 0, "
                + COLUMN_BATCH_ID_FK + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_BATCH_ID_FK + ") REFERENCES "
                + TABLE_BATCHES + "(" + COLUMN_BATCH_ID + "))"
                ;
        db.execSQL(createStudentsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATCHES);
        onCreate(db);
    }

    // عمليات الدفعات
    public boolean addBatch(String name, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BATCH_NAME, name);
        values.put(COLUMN_BATCH_NUMBER, number);
        db.insert(TABLE_BATCHES, null, values);
        return true;
    }

    public Cursor getAllBatches() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_BATCHES, null, null, null, null, null, null);
    }

    // عمليات الطلاب
    public boolean addStudent(String name,  long batchId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, name);
        values.put(COLUMN_BATCH_ID_FK, batchId);
        values.put(COLUMN_IS_DONE, 0);
        db.insert(TABLE_STUDENTS, null, values);
        return true;
    }

    public Cursor getStudentsByBatch(long batchId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_STUDENTS, null,
                COLUMN_BATCH_ID_FK + " = ?",
                new String[]{String.valueOf(batchId)},
                null, null, null);
    }
    public void addStudentPoints(long studentId, int pointsToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_POINTS +" , "+ COLUMN_IS_DONE + " , " + COLUMN_COMBO_7 + " FROM " + TABLE_STUDENTS +
                        " WHERE " + COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)});


        int currentPoints = 0;
        int currentPoibnt_is_done = 0;
        int Cu_Com_7 = 0;
        if (cursor.moveToFirst()) {
            currentPoints = cursor.getInt(0);
            currentPoibnt_is_done = cursor.getInt(1);
            Cu_Com_7 = cursor.getInt(2);
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_POINTS, currentPoints + pointsToAdd);

        db.update(TABLE_STUDENTS, values,
                COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)});
         if (pointsToAdd == 0 || pointsToAdd == -2)
         {
             ContentValues values_2 = new ContentValues();
             values_2.put(COLUMN_IS_DONE, currentPoibnt_is_done + 1);
             db.update(TABLE_STUDENTS, values_2,
                     COLUMN_STUDENT_ID + " = ?",
                     new String[]{String.valueOf(studentId)});
         }
         else
         {

             ContentValues values_2 = new ContentValues();
             if (currentPoibnt_is_done == 0)
             {
                 values_2.put(COLUMN_IS_DONE, 0);
             }
             else if(currentPoibnt_is_done > 0)
             {
                 values_2.put(COLUMN_IS_DONE, currentPoibnt_is_done -1);
             }

             db.update(TABLE_STUDENTS, values_2,
                     COLUMN_STUDENT_ID + " = ?",
                     new String[]{String.valueOf(studentId)});
         }
         if (pointsToAdd != 7)
         {
             ContentValues values_3 = new ContentValues();
             values_3.put(COLUMN_COMBO_7, 0);

             db.update(TABLE_STUDENTS, values_3,
                     COLUMN_STUDENT_ID + " = ?",
                     new String[]{String.valueOf(studentId)});
         }
         if (pointsToAdd == 7)
         {
             ContentValues values_3 = new ContentValues();
             values_3.put(COLUMN_COMBO_7, Cu_Com_7 + 1);

             db.update(TABLE_STUDENTS, values_3,
                     COLUMN_STUDENT_ID + " = ?",
                     new String[]{String.valueOf(studentId)});
         }
        db.close();
    }
    // دالة تحديث اسم الطالب
    public boolean updateStudent(long studentId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, newName);
        int rowsAffected = db.update(TABLE_STUDENTS, values,
                COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)});
        return rowsAffected > 0;
    }

    // دالة حذف طالب
    public boolean deleteStudent(long studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_STUDENTS,
                COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)});
        return rowsAffected > 0;
    }

    // دالة حذف دفعة (مع حذف جميع طلابها)
    public boolean deleteBatch(long batchId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // أولاً حذف جميع طلاب الدفعة
        db.delete(TABLE_STUDENTS,
                COLUMN_BATCH_ID_FK + " = ?",
                new String[]{String.valueOf(batchId)});

        // ثم حذف الدفعة
        int rowsAffected = db.delete(TABLE_BATCHES,
                COLUMN_BATCH_ID + " = ?",
                new String[]{String.valueOf(batchId)});

        return rowsAffected > 0;
    }
}