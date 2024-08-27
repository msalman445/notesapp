package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    private static DB instance;
    public static final String DB_NAME = "DatabaseNotes";
    public static final int DB_VERSION = 1;
    long rowId;

    private DB(Context context){
        super(context, DB_NAME, null, DB_VERSION);

    }

    public static DB getInstance(Context context){
        if (instance == null){
            instance = new DB(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Note.DROP_TABLE);
        db.execSQL(Note.CREATE_TABLE);
    }

    public boolean insertNote(Note note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Note.COL_TITLE, note.getNoteTitle());
        contentValues.put(Note.COL_TIME_SPAN, note.getTimeSpan());
        contentValues.put(Note.COL_DESCRIPTION, note.getNoteDescription());
        contentValues.put(Note.COL_IMAGE_PATH, note.getImagePath());

        try {
            rowId = db.insert(Note.TABLE_NAME, null, contentValues);
        }catch (Exception ex){
            return false;
        }
        return rowId != -1;
    }

    public boolean updateNote(Note note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Note.COL_TITLE, note.getNoteTitle());
        contentValues.put(Note.COL_TIME_SPAN, note.getTimeSpan());
        contentValues.put(Note.COL_DESCRIPTION, note.getNoteDescription());
        contentValues.put(Note.COL_IMAGE_PATH, note.getImagePath());

        long rowId;
        try {
            rowId = db.update(Note.TABLE_NAME, contentValues, Note.COL_ID + " = ?" ,new String[]{String.valueOf(note.getId())});
        }catch (Exception ex){
            return false;
        }

        return rowId != -1;
    }

    public boolean deleteNote(Note note){
        SQLiteDatabase db = getWritableDatabase();

        long rowId;
        try {
            rowId = db.delete(Note.TABLE_NAME,Note.COL_ID + " = ?" , new String[]{String.valueOf(note.getId())});
        }catch (Exception ex){
            return false;
        }

        return rowId != -1;
    }

    public List<Note> viewAllNotes(String sqlQuery){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        List<Note> notes = new ArrayList<>(cursor.getCount());

        if (cursor.moveToFirst()){
            do {
                Note note = new Note();

                int index = cursor.getColumnIndex(Note.COL_ID);
                note.setId(cursor.getInt(index));

                index = cursor.getColumnIndex(Note.COL_TITLE);
                note.setNoteTitle(cursor.getString(index));

                index = cursor.getColumnIndex(Note.COL_DESCRIPTION);
                note.setNoteDescription(cursor.getString(index));

                index = cursor.getColumnIndex(Note.COL_TIME_SPAN);
                note.setTimeSpan(cursor.getString(index));

                index = cursor.getColumnIndex(Note.COL_IMAGE_PATH);
                note.setImagePath(cursor.getString(index));

                notes.add(note);
            }while (cursor.moveToNext());
        }
        return notes;
    }

    public boolean favNote(Note note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Note.COL_IS_FAVORITE, note.isFavorite());

        long rowId;
        try {
            rowId = db.update(Note.TABLE_NAME, contentValues, Note.COL_ID + " = ?" ,new String[]{String.valueOf(note.getId())});
        }catch (Exception ex){
            return false;
        }

        return rowId != -1;
    }

    public boolean delNote(Note note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Note.COL_IS_DELETED, note.isDeleted());

        long rowId;
        try {
            rowId = db.update(Note.TABLE_NAME, contentValues, Note.COL_ID + " = ?" ,new String[]{String.valueOf(note.getId())});
        }catch (Exception ex){
            return false;
        }

        return rowId != -1;
    }
}
