package com.project.literarycatalog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.project.literarycatalog/databases/";
    private static String DB_NAME = "mydb.db";
    private static final int SCHEMA = 1; // версия базы данных
    public static final String TABLE = "books";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_YEAR_OF_CREATION = "yearOfCreation";
    public static final String COLUMN_CITY_OF_CREATION = "cityOfCreation";
    public static final String COLUMN_PUBLISHING_HOUSE = "publishingHouse";
    public static final String COLUMN_NUMBER_OF_PAGES = "numberOfPages";
    public static final String COLUMN_IMAGE = "image";

    public SQLiteDatabase database;
    private Context myContext;

    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, SCHEMA);
        this.myContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS books ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_AUTHOR + " TEXT, "
                + COLUMN_IMAGE + " BLOB, "
                + COLUMN_YEAR_OF_CREATION + " INTEGER, "
                + COLUMN_CITY_OF_CREATION + " TEXT, "
                + COLUMN_PUBLISHING_HOUSE + " TEXT, "
                + COLUMN_NUMBER_OF_PAGES + " INTEGER);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }

    public void open() throws SQLException {
        String path = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }
}
