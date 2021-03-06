package se.arvidbodkth.laboration41.SQLitePackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import se.arvidbodkth.laboration41.NotePackage.Note;


/**
 * Created by Arvid Bodin and Mattias Grehnik on 2016-01-03.
 *
 * A helper Class for the SQL database.
 */
public class NoteDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";

    private SQLiteDatabase db;

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            NoteContract.NoteEntry.TABLE_NAME + " (" +
            NoteContract.NoteEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            NoteContract.NoteEntry.NOTE_TITLE + " TEXT," +
            NoteContract.NoteEntry.NOTE_DATE + " TEXT," +
            NoteContract.NoteEntry.NOTE_BODY + " TEXT," +
            NoteContract.NoteEntry.IMAGE_NAME + " TEXT" +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NoteContract.NoteEntry.TABLE_NAME;

    /**
     * Construcor for the helper.
     * @param context Context, the context for the db.
     */
    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME
                , null, DATABASE_VERSION);
    }

    /**
     * Creates the db.
     * @param db SQLiteDatabase to create.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Removes all the rows form the db.
     */
    public void removeAll() {
        db = this.getWritableDatabase();
        db.delete(NoteContract.NoteEntry.TABLE_NAME, null, null);
        db.close();
    }

    /**
     * Removes a column from the db.
     * @param id String with the id of from the note/row to remove.
     */
    public void removeOne(String id) {
        db = this.getWritableDatabase();

        db.delete(NoteContract.NoteEntry.TABLE_NAME
                , NoteContract.NoteEntry.COLUMN_NAME_ID + "=?", new String[]{id});
        db.close();
        getAll();
    }

    /**
     * Returns all the columns as notes from the db.
     * @return ArrayList with all the notes.
     */
    public ArrayList<Note> getAll() {
        ArrayList<Note> notes = new ArrayList<>();
        db = this.getReadableDatabase();

        String[] projection = {
                NoteContract.NoteEntry.COLUMN_NAME_ID,
                NoteContract.NoteEntry.NOTE_TITLE,
                NoteContract.NoteEntry.NOTE_DATE,
                NoteContract.NoteEntry.NOTE_BODY,
                NoteContract.NoteEntry.IMAGE_NAME
        };

        Cursor c = db.query(
                NoteContract.NoteEntry.TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();

        while (!c.isAfterLast()) {
            notes.add(new Note(
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NAME_ID)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_DATE)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_BODY)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.IMAGE_NAME))
            ));
            c.moveToNext();
        }

        c.close();
        db.close();
        return notes;
    }

    /**
     * Update a column with the info from the given note.
     * @param note Note the note to update.
     */
    public void updateNote(Note note) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.NOTE_TITLE, note.getTitle());
        values.put(NoteContract.NoteEntry.NOTE_DATE, note.getDate());
        values.put(NoteContract.NoteEntry.NOTE_BODY, note.getBody());
        values.put(NoteContract.NoteEntry.IMAGE_NAME, note.getImageName());

        db.update(NoteContract.NoteEntry.TABLE_NAME, values
                , NoteContract.NoteEntry.COLUMN_NAME_ID + "=" + note.getId()
                , null);
        db.close();
    }

    /**
     * Creates a new column for the given note.
     * @param note Note the note to add.
     */
    public void addNote(Note note) {
        db = this.getWritableDatabase();

        db.execSQL("INSERT INTO NOTE (TITLE, DATE, CONTENT, IMAGENAME) VALUES ('"
                        + note.getTitle() + "', '"
                        + note.getDate() + "', '"
                        + note.getBody() + "', '"
                        + note.getImageName() + "')"
        );
        db.close();
    }

    /**
     * Searches the title row in all the columns for the given string.
     * @param param String to search for.
     * @return ArrayList with all the hits
     */
    public ArrayList<Note> searchTitle(String param) {
        ArrayList<Note> notes = new ArrayList<>();
        db = this.getReadableDatabase();

        String[] projection = {
                NoteContract.NoteEntry.COLUMN_NAME_ID,
                NoteContract.NoteEntry.NOTE_TITLE,
                NoteContract.NoteEntry.NOTE_DATE,
                NoteContract.NoteEntry.NOTE_BODY,
                NoteContract.NoteEntry.IMAGE_NAME
        };

        Cursor c = db.query(
                NoteContract.NoteEntry.TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                NoteContract.NoteEntry.NOTE_TITLE
                        + " LIKE '%" + param + "%'",       // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        c.moveToFirst();

        while (!c.isAfterLast()) {
            notes.add(new Note(
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NAME_ID)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_DATE)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_BODY)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.IMAGE_NAME))
            ));

            c.moveToNext();
        }

        c.close();
        db.close();
        return notes;
    }

    /**
     * Searches the date row in all columns for the given string.
     * @param param String to search for.
     * @return ArrayList for all the hits.
     */
    public ArrayList<Note> searchDate(String param) {
        System.out.println("Searching for: " + param);
        ArrayList<Note> notes = new ArrayList<>();
        db = this.getReadableDatabase();

        String[] projection = {
                NoteContract.NoteEntry.COLUMN_NAME_ID,
                NoteContract.NoteEntry.NOTE_TITLE,
                NoteContract.NoteEntry.NOTE_DATE,
                NoteContract.NoteEntry.NOTE_BODY,
                NoteContract.NoteEntry.IMAGE_NAME
        };

        Cursor c = db.query(
                NoteContract.NoteEntry.TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                NoteContract.NoteEntry.NOTE_DATE
                        + " LIKE '%" + param + "%'",       // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        c.moveToFirst();

        while (!c.isAfterLast()) {
            notes.add(new Note(
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NAME_ID)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_DATE)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_BODY)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.IMAGE_NAME))
            ));

            c.moveToNext();
        }

        c.close();
        db.close();
        return notes;
    }

    /**
     * Searches the body row in all the columns for the given string.
     * @param param String to search for.
     * @return ArrayList for all the hits.
     */
    public ArrayList<Note> searchBody(String param) {
        System.out.println("Searching for: " + param);
        ArrayList<Note> notes = new ArrayList<>();
        db = this.getReadableDatabase();

        String[] projection = {
                NoteContract.NoteEntry.COLUMN_NAME_ID,
                NoteContract.NoteEntry.NOTE_TITLE,
                NoteContract.NoteEntry.NOTE_DATE,
                NoteContract.NoteEntry.NOTE_BODY,
                NoteContract.NoteEntry.IMAGE_NAME
        };

        Cursor c = db.query(
                NoteContract.NoteEntry.TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                NoteContract.NoteEntry.NOTE_BODY
                        + " LIKE '%" + param + "%'",       // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();

        while (!c.isAfterLast()) {
            notes.add(new Note(
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NAME_ID)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_DATE)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.NOTE_BODY)),
                    c.getString(c.getColumnIndexOrThrow(NoteContract.NoteEntry.IMAGE_NAME))
            ));
            c.moveToNext();
        }

        c.close();
        db.close();
        return notes;
    }
}
