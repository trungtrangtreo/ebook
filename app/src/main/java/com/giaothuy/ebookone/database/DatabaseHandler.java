package com.giaothuy.ebookone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 1 on 2/23/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "manager_file";

    // Contacts table name
    private static final String TABLE_PAGE = "table_page";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PAGE = "number_page";
    private static final String KEY_TITLE = "title";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PAGE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PAGE + " TEXT," + KEY_TITLE + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGE);
        // Create tables again
        onCreate(db);
    }

    public void addPage(String page, String title) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PAGE, page); // Contact Name
        values.put(KEY_TITLE, title); // Contact Name
        // Inserting Row
        db.insert(TABLE_PAGE, null, values);
        db.close(); // Closing database connection
    }

    public int getPageCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updatePage(String page, String title) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PAGE, page);
        values.put(KEY_TITLE, title);
        // updating row
        return db.update(TABLE_PAGE, values, KEY_ID + " = ?",
                new String[]{"1"});
    }

    public int getPage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PAGE, new String[]{KEY_ID,
                        KEY_PAGE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        int page = Integer.parseInt(cursor.getString(1));
        Log.e("1111", cursor.getString(1));

        // return contact
        return page;
    }

    public String getTitle(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PAGE, new String[]{KEY_ID,
                        KEY_PAGE,KEY_TITLE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        String title = cursor.getString(2);
        // return contact
        return title;
    }

}
