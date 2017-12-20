package com.example.fauza.zmdb.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fauza.zmdb.R;
import com.example.fauza.zmdb.data.MovieDetailsContract;
import com.example.fauza.zmdb.data.MovieDetailsDbHelper;

public class MovieDetailFavoriteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MovieDetailFavorite";

    private String movieId;
    private MovieDetailsDbHelper dbHelper;
    private SQLiteDatabase mDb;

    private Toolbar toolbarMain;
    private ConstraintLayout layoutMovieDetailActivity;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayoutMovieDetail;
    private ImageView imageViewBackdrop;
    private ImageView imageViewPoster;
    private TextView textViewMovieTitle;
    private TextView textViewRatingAverage;
    private TextView textViewVoteCount;
    private TextView textViewGenres;
    private ImageView imageViewFavoriteNo;
    private ImageView imageViewFavoriteYes;
    private RelativeLayout relativeLayoutTrailer;
    private TextView textViewOverview;
    private ProgressBar progressBarReview;
    private TextView textViewReviewLabel;
    private TextView textViewReviewContent;
    private TextView textViewMoreReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        dbHelper = new MovieDetailsDbHelper(MovieDetailFavoriteActivity.this);
        mDb = dbHelper.getWritableDatabase();

        layoutMovieDetailActivity = findViewById(R.id.layout_activity_movie_detail);
        toolbarMain = findViewById(R.id.toolbar_main);
        progressBar = findViewById(R.id.progressbar);
        constraintLayoutMovieDetail = findViewById(R.id.constraintLayout_movie_detail);
        imageViewBackdrop = findViewById(R.id.imageView_backdrop);
        imageViewPoster = findViewById(R.id.imageView_poster);
        textViewMovieTitle = findViewById(R.id.textView_movie_title);
        textViewRatingAverage = findViewById(R.id.textView_rating_average);
        textViewVoteCount = findViewById(R.id.textView_vote_count);
        textViewGenres = findViewById(R.id.textView_genres);
        imageViewFavoriteNo = findViewById(R.id.imageButton_favorite_no);
        imageViewFavoriteYes = findViewById(R.id.imageButton_favorite_yes);
        relativeLayoutTrailer = findViewById(R.id.relativeLayout_play_trailer);
        textViewOverview = findViewById(R.id.textView_overview);
        textViewReviewLabel = findViewById(R.id.textView_review_label);
        progressBarReview = findViewById(R.id.progressbarReviewContent);
        textViewReviewContent = findViewById(R.id.textView_review_content);
        textViewMoreReview = findViewById(R.id.textView_more_review);

        imageViewFavoriteNo.setVisibility(View.INVISIBLE);
        imageViewFavoriteYes.setVisibility(View.INVISIBLE);
        textViewMoreReview.setVisibility(View.INVISIBLE);
        constraintLayoutMovieDetail.setVisibility(View.INVISIBLE);

        imageViewFavoriteNo.setOnClickListener(this);
        imageViewFavoriteYes.setOnClickListener(this);
        relativeLayoutTrailer.setOnClickListener(this);
        textViewMoreReview.setOnClickListener(this);

        Intent intent = getIntent();
        String movieId = intent.getStringExtra(getString(R.string.movieId));
        this.movieId = movieId;

        toolbarMain.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbarMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Cursor cursor = getMovieDetail();
        int movieIdIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_MOVIE_ID
        );
        int backDropPathIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_BACKDROP_PATH
        );
        int posterPathIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_POSTER_PATH
        );
        int titleIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_TITLE
        );
        int voteAverageIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_VOTE_AVERAGE
        );
        int voteCountIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_VOTE_COUNT
        );
        int genresIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_GENRES
        );
        int overviewIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_OVERVIEW
        );

        int movieReviewIndex = cursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_REVIEW
        );
        cursor.moveToPosition(0);
        constraintLayoutMovieDetail.setVisibility(View.VISIBLE);
        String mMovieId = cursor.getString(movieIdIndex);
        String backdropPath = cursor.getString(backDropPathIndex);
        String posterPath = cursor.getString(posterPathIndex);
        String title = cursor.getString(titleIndex);
        String voteAverage = cursor.getString(voteAverageIndex);
        String voteCount = cursor.getString(voteCountIndex);
        String overview = cursor.getString(overviewIndex);
        String genres = cursor.getString(genresIndex);
        String review = cursor.getString(movieReviewIndex);

        Glide.with(MovieDetailFavoriteActivity.this)
                .load(backdropPath)
                .apply(new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.backdropplaceholder))
                .into(imageViewBackdrop);
        Glide.with(MovieDetailFavoriteActivity.this)
                .load(posterPath)
                .apply(new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.posterplaceholder))
                .into(imageViewPoster);
        textViewMovieTitle.setText(title);
        textViewRatingAverage.setText(voteAverage);
        textViewVoteCount.setText(voteCount);
        textViewOverview.setText(overview);
        textViewGenres.setText(genres);
        textViewReviewContent.setText(review);

        isFavorite(this.movieId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relativeLayout_play_trailer:
                Uri youtubTrailer = Uri.parse(relativeLayoutTrailer.getTag().toString());
                if (youtubTrailer != null) {
                    Intent intentApp = new Intent(Intent.ACTION_VIEW, youtubTrailer);
                    Intent intentWeb = new Intent(Intent.ACTION_VIEW, youtubTrailer);
                    try {
                        MovieDetailFavoriteActivity.this.startActivity(intentApp);
                    } catch (ActivityNotFoundException ex) {
                        MovieDetailFavoriteActivity.this.startActivity(intentWeb);
                    }
                }
                break;
            case R.id.imageButton_favorite_no:
                saveFavorite();
                break;
            case R.id.imageButton_favorite_yes:
                deleteFavorite(movieId);
                break;
        }
    }

    private void saveFavorite() {
        String movieId = this.movieId;
        String backdropPath = imageViewBackdrop.getTag(R.id.imageView_backdrop).toString();
        String posterPath = imageViewPoster.getTag(R.id.imageView_poster).toString();
        String title = textViewMovieTitle.getText().toString();
        String voteAverage = textViewRatingAverage.getText().toString();
        String voteCount = textViewVoteCount.getText().toString();
        String genres = textViewGenres.getText().toString();
        String overView = textViewOverview.getText().toString();
        String review = textViewReviewContent.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_MOVIE_ID, movieId);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_BACKDROP_PATH, backdropPath);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_POSTER_PATH, posterPath);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_POSTER_PATH, posterPath);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_TITLE, title);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_VOTE_COUNT, voteCount);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_GENRES, genres);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_OVERVIEW, overView);
        contentValues.put(MovieDetailsContract.MoviDetailsEntry.COLUMN_REVIEW, review);

        long dbResult = mDb.insert(MovieDetailsContract.MoviDetailsEntry.TABLE_NAME, null, contentValues);
        if (dbResult > 0) {
            imageViewFavoriteNo.setVisibility(View.INVISIBLE);
            imageViewFavoriteYes.setVisibility(View.VISIBLE);
            Snackbar.make(layoutMovieDetailActivity, "Added to Favorite.", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(layoutMovieDetailActivity, "ERROR: Add to Favorite failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    public Cursor getMovieDetail() {
        String[] tableColumns = new String[]{
                "movie_id",
                "backdrop_path",
                "poster_path",
                "title",
                "vote_average",
                "vote_count",
                "genres",
                "overview",
                "review"
        };
        String whereClause = "movie_id = ?";
        String[] whereArgs = new String[]{
                this.movieId
        };

        return mDb.query(
                MovieDetailsContract.MoviDetailsEntry.TABLE_NAME,
                tableColumns,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
    }

    private void deleteFavorite(String movieId) {
        long dbResult = mDb.delete(MovieDetailsContract.MoviDetailsEntry.TABLE_NAME,
                "movie_id = " + movieId, null);
        if (dbResult > 0) {
            imageViewFavoriteNo.setVisibility(View.VISIBLE);
            imageViewFavoriteYes.setVisibility(View.INVISIBLE);
            Snackbar.make(layoutMovieDetailActivity, "Removed from Favorite.", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(layoutMovieDetailActivity, "ERROR: Remove from Favorite failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void isFavorite(String movieId) {
        Cursor mCursor = mDb.rawQuery("SELECT movie_id FROM movie_details_table WHERE movie_id = " + movieId, null);
        if (mCursor.getCount() >= 1) {
            imageViewFavoriteNo.setVisibility(View.INVISIBLE);
            imageViewFavoriteYes.setVisibility(View.VISIBLE);
        } else {
            imageViewFavoriteNo.setVisibility(View.VISIBLE);
            imageViewFavoriteYes.setVisibility(View.INVISIBLE);
        }
    }
}
