package com.example.fauza.zmdb.data;

import android.provider.BaseColumns;

public class MovieDetailsContract {
    public static final class MoviDetailsEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie_details_table";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_REVIEW = "review";
    }
}
