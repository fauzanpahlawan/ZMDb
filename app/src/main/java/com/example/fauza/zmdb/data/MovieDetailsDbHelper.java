package com.example.fauza.zmdb.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDetailsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favouriteMovie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDetailsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITE_MOVIE_TABLE =
                "CREATE TABLE " +
                        MovieDetailsContract.MoviDetailsEntry.TABLE_NAME + "(" +
                        MovieDetailsContract.MoviDetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_VOTE_COUNT + " TEXT NOT NULL," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_GENRES + " TEXT NOT NULL," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                        MovieDetailsContract.MoviDetailsEntry.COLUMN_REVIEW + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
                + MovieDetailsContract.MoviDetailsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
