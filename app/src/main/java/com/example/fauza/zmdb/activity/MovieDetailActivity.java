package com.example.fauza.zmdb.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.example.fauza.zmdb.model.MovieDetail;
import com.example.fauza.zmdb.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MovieDetailActivity";

    private String movieId;
    private MovieDetail movieDetail;

    private MovieDetailsDbHelper dbHelper;
    private SQLiteDatabase mDb;
    private Cursor mCursor;

    private Toolbar toolbarMain;
    private ConstraintLayout layoutMovieDetailActivity;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;
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

    private static final int BACKDROP_TAG = 71;
    private static final int POSTER_TAG = 72;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        dbHelper = new MovieDetailsDbHelper(MovieDetailActivity.this);
        mDb = dbHelper.getWritableDatabase();

        layoutMovieDetailActivity = findViewById(R.id.layout_activity_movie_detail);
        toolbarMain = findViewById(R.id.toolbar_main);
        progressBar = findViewById(R.id.progressbar);
        constraintLayout = findViewById(R.id.constraintLayout_movie_detail);
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
        constraintLayout.setVisibility(View.INVISIBLE);

        imageViewFavoriteNo.setOnClickListener(this);
        imageViewFavoriteYes.setOnClickListener(this);
        relativeLayoutTrailer.setOnClickListener(this);
        textViewMoreReview.setOnClickListener(this);

        Intent intent = getIntent();
        String movieId = intent.getStringExtra(getString(R.string.movieId));
        this.movieId = movieId;
        Uri uriMovieDetails = NetworkUtils.getMovieDetails(MovieDetailActivity.this, movieId);
        new NetworkTaskMovieDetail().execute(uriMovieDetails);
        Uri uriTrailerKey = NetworkUtils.getMovieVideos(MovieDetailActivity.this, movieId);
        new NetworkTaskVideoKey().execute(uriTrailerKey);
        Uri uriFirstReview = NetworkUtils.getMovieReviews(MovieDetailActivity.this, movieId);
        new NetworkTaskMovieReview().execute(uriFirstReview);

        toolbarMain.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbarMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
                        MovieDetailActivity.this.startActivity(intentApp);
                    } catch (ActivityNotFoundException ex) {
                        MovieDetailActivity.this.startActivity(intentWeb);
                    }
                }
                break;
            case R.id.textView_more_review:
                Intent intent = new Intent(MovieDetailActivity.this, MoreReviewActivity.class);
                intent.putExtra(getString(R.string.movieId), this.movieId);
                MovieDetailActivity.this.startActivity(intent);
                break;
            case R.id.imageButton_favorite_no:
                saveFavorite();
                Cursor favCursor = mDb.rawQuery("SELECT * FROM movie_details_table WHERE movie_id = " + movieId, null);
                if (favCursor.moveToFirst()) {
                    while (!favCursor.isAfterLast()) {
                        Log.w(TAG, favCursor.getString(favCursor.getColumnIndex("title")));
                        favCursor.moveToNext();
                    }
                }
                break;
            case R.id.imageButton_favorite_yes:
                deleteFavorite(movieId);
                Cursor mCursor = mDb.rawQuery("SELECT * FROM movie_details_table WHERE movie_id = " + movieId, null);
                if (mCursor.moveToFirst()) {
                    while (!mCursor.isAfterLast()) {
                        Log.w(TAG, mCursor.getString(mCursor.getColumnIndex("title")));
                        mCursor.moveToNext();
                    }
                }
                break;
        }
    }

    public MovieDetail getMovieDetails(String jsonData) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new MovieDetail(jsonObject);
    }

    public String getTrailerKey(String jsonData) {
        String trailerKey = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            JSONObject result = jsonArray.getJSONObject(0);
            trailerKey = result.getString("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerKey;
    }

    public String getFirstReview(String jsonData) {
        StringBuilder firstReviewBuilder = new StringBuilder();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            JSONObject result = jsonArray.getJSONObject(0);
            firstReviewBuilder.append(result.getString("author"));
            firstReviewBuilder.append(": \n");
            firstReviewBuilder.append(result.getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return firstReviewBuilder.toString();

    }

    public String getGenres(ArrayList<String> genres) {
        if (genres.size() == 0) {
            return "";
        } else {

            StringBuilder genresBuilder = new StringBuilder();
            for (String str : movieDetail.genres) {
                genresBuilder.append(str).append(", ");
            }
            String genresResult = genresBuilder.substring(0, genresBuilder.length() - 2) + ".";
            return genresResult;
        }
    }

    class NetworkTaskMovieDetail extends AsyncTask<Uri, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Uri... uris) {
            URL movieUrl = null;
            try {
                movieUrl = new URL(uris[0].toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (movieUrl != null) {
                try {
                    return NetworkUtils.getResponseFromHttpUrl(movieUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
            movieDetail = getMovieDetails(s);
            textViewMovieTitle.setText(String.format("%s (%s)", movieDetail.movieTitle, movieDetail.releaseDate));
            textViewRatingAverage.setText(String.format("%s/10", movieDetail.voteAverage));
            textViewGenres.setText(getGenres(movieDetail.genres));
            textViewVoteCount.setText(NumberFormat.getNumberInstance(Locale.US).format(Float.valueOf(movieDetail.voteCount)));
            textViewOverview.setText(movieDetail.overview);
            Uri uriBackDrop = NetworkUtils.getTMDBImage(MovieDetailActivity.this, movieDetail.backdrop_path);
            Glide.with(MovieDetailActivity.this)
                    .load(uriBackDrop)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.backdropplaceholder))
                    .into(imageViewBackdrop);
            imageViewBackdrop.setTag(R.id.imageView_backdrop, uriBackDrop);
            Uri uriPoster = NetworkUtils.getTMDBImage(MovieDetailActivity.this, movieDetail.posterPath);
            Glide.with(MovieDetailActivity.this)
                    .load(uriPoster)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.posterplaceholder))
                    .into(imageViewPoster);
            imageViewPoster.setTag(R.id.imageView_poster, uriPoster);
        }
    }

    class NetworkTaskVideoKey extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            URL movieUrl = null;
            try {
                movieUrl = new URL(uris[0].toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (movieUrl != null) {
                try {
                    return NetworkUtils.getResponseFromHttpUrl(movieUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String key = getTrailerKey(s);
            Uri uri = NetworkUtils.getYouTubeLink(MovieDetailActivity.this, key);
            relativeLayoutTrailer.setTag(uri);
        }
    }

    class NetworkTaskMovieReview extends AsyncTask<Uri, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarReview.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Uri... uris) {
            URL movieUrl = null;
            try {
                movieUrl = new URL(uris[0].toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (movieUrl != null) {
                try {
                    return NetworkUtils.getResponseFromHttpUrl(movieUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBarReview.setVisibility(View.INVISIBLE);
            textViewMoreReview.setVisibility(View.VISIBLE);
            isFavorite(movieId);
            String firstReview = getFirstReview(s);
            textViewReviewContent.setText(firstReview);
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
