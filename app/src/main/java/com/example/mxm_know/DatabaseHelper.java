package com.example.mxm_know;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "knowledge.db";
    private static final int DATABASE_VERSION = 1;

    // جداول الدفعات
    public static final String TABLE_BATCHES = "batches";
    public static final String COLUMN_BATCH_ID = "batch_id";
    public static final String COLUMN_BATCH_NAME = "batch_name";
    public static final String COLUMN_BATCH_NUMBER = "batch_number";

    // جداول الطلاب
    public static final String TABLE_STUDENTS = "students";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_STUDENT_NAME = "student_name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_POINTS = "points";
    public static final String COLUMN_BATCH_ID_FK = "batch_id";

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
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_POINTS + " INTEGER DEFAULT 0, "
                + COLUMN_BATCH_ID_FK + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_BATCH_ID_FK + ") REFERENCES "
                + TABLE_BATCHES + "(" + COLUMN_BATCH_ID + "))";
        db.execSQL(createStudentsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATCHES);
        onCreate(db);
    }

    // عمليات الدفعات
    public long addBatch(String name, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BATCH_NAME, name);
        values.put(COLUMN_BATCH_NUMBER, number);
        return db.insert(TABLE_BATCHES, null, values);
    }

    public Cursor getAllBatches() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_BATCHES, null, null, null, null, null, null);
    }

    // عمليات الطلاب
    public long addStudent(String name, String phone, long batchId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_BATCH_ID_FK, batchId);
        return db.insert(TABLE_STUDENTS, null, values);
    }

    public void updatePoints(long studentId, int points) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db_read = this.getReadableDatabase();
        int pp= 0;
        ContentValues values = new ContentValues();
        Cursor cursor =  db_read.query(TABLE_STUDENTS , new String[]{COLUMN_POINTS} , COLUMN_STUDENT_ID + " = ?" ,new String[]  {String.valueOf(studentId)} ,null,null ,null);
        if (cursor != null && cursor.moveToFirst())
        {
            int columnIndex = cursor.getColumnIndex(COLUMN_POINTS);
            if (columnIndex != -1) {
                pp = cursor.getInt(columnIndex);
            }
            cursor.close();
        }
        values.put(COLUMN_POINTS, points + pp);
        db.update(TABLE_STUDENTS, values,
                COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
    }

    public Cursor getStudentsByBatch(long batchId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_STUDENTS, null,
                COLUMN_BATCH_ID_FK + " = ?",
                new String[]{String.valueOf(batchId)},
                null, null, null);
    }
}