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
    public static final String COLUMN_IS_DONE = "is_done";


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
                + COLUMN_IS_DONE + " INTEGER DEFAULT 0, "
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
        values.put(COLUMN_IS_DONE, 0);

        return db.insert(TABLE_STUDENTS, null, values);
    }


//    public Cursor getStudentsByBatch(long batchId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.query(TABLE_STUDENTS,
//                new String[]{COLUMN_STUDENT_ID, COLUMN_STUDENT_NAME,
//                        COLUMN_PHONE, COLUMN_POINTS, COLUMN_IS_DONE},
//                COLUMN_BATCH_ID_FK + " = ?",
//                new String[]{String.valueOf(batchId)},
//                null, null, null);
//    }
//
    public Cursor getStudentsByBatch(long batchId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_STUDENTS, null,
                COLUMN_BATCH_ID_FK + " = ?",
                new String[]{String.valueOf(batchId)},
                null, null, null);
    }
    public void addStudentPoints(long studentId, int pointsToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_POINTS + " FROM " + TABLE_STUDENTS +
                        " WHERE " + COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)});

        int currentPoints = 0;
        if (cursor.moveToFirst()) {
            currentPoints = cursor.getInt(0);
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_POINTS, currentPoints + pointsToAdd);

        db.update(TABLE_STUDENTS, values,
                COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)});
         if (pointsToAdd == 0)
         {
             ContentValues values_2 = new ContentValues();
             values_2.put(COLUMN_IS_DONE, 1);

             db.update(TABLE_STUDENTS, values_2,
                     COLUMN_STUDENT_ID + " = ?",
                     new String[]{String.valueOf(studentId)});
         }
         else
         {
             ContentValues values_2 = new ContentValues();
             values_2.put(COLUMN_IS_DONE, 0);

             db.update(TABLE_STUDENTS, values_2,
                     COLUMN_STUDENT_ID + " = ?",
                     new String[]{String.valueOf(studentId)});
         }
        db.close();
    }
}