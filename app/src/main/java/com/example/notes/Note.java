package com.example.notes;

import java.util.Objects;

public class Note{
    private String noteTitle;
    private String timeSpan;
    private String noteDescription;
    private String imagePath;
    private long id;
    private boolean isFavorite;
    private boolean isDeleted;

    public static final String TABLE_NAME = "Notes";
    public static String COL_ID = "NotesId";
    public static final String COL_TITLE = "NotesTitle";
    public static final String COL_DESCRIPTION = "NotesDescription";
    public static final String COL_TIME_SPAN = "NotesTimeSpan";
    public static final String COL_IMAGE_PATH = "ImagePath";
    public static final String COL_IS_FAVORITE = "Favorite";
    public static final String COL_IS_DELETED = "Recycle";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TITLE + " TEXT, " +
            COL_TIME_SPAN + " TEXT, " +
            COL_DESCRIPTION + " TEXT, " +
            COL_IMAGE_PATH + " TEXT, " +
            COL_IS_FAVORITE + " TEXT," +
            COL_IS_DELETED + " TEXT DEFAULT FALSE)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String VIEW_ALL = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_IS_DELETED + " = FALSE";
    public static final String VIEW_DELETED = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_IS_DELETED + " = TRUE";
    public static final String VIEW_FAVORITES = "SELECT * FROM " + TABLE_NAME + " WHERE "+ COL_IS_FAVORITE + " = TRUE AND "+ COL_IS_DELETED + " = FALSE";

//    Favorites Table

    public Note() {
    }


    public Note(String noteTitle, String timeSpan, String noteDescription, String imagePath, long id, boolean isFavorite, boolean isDeleted) {
        this.noteTitle = noteTitle;
        this.timeSpan = timeSpan;
        this.noteDescription = noteDescription;
        this.imagePath = imagePath;
        this.id = id;
        this.isFavorite = isFavorite;
        this.isDeleted = isDeleted;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public void setNoteDescription(String noteDescription) {
        this.noteDescription = noteDescription;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id && Objects.equals(noteTitle, note.noteTitle) && Objects.equals(timeSpan, note.timeSpan) && Objects.equals(noteDescription, note.noteDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteTitle, timeSpan, noteDescription, id);
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteTitle='" + noteTitle + '\'' +
                ", timeSpan='" + timeSpan + '\'' +
                ", noteDescription='" + noteDescription + '\'' +
                ", id=" + id +
                '}';
    }

    public static Note getNoteForId(long passedId, DB db){

        for (Note note : db.viewAllNotes(Note.VIEW_ALL)) {
            if (note.getId() == passedId){
                return note;
            }
        }
        return null;
    }
}
