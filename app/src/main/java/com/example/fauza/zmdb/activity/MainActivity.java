package com.example.fauza.zmdb.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.fauza.zmdb.R;
import com.example.fauza.zmdb.adapter.MoviePosterAdapter;
import com.example.fauza.zmdb.adapter.MoviePosterFavoriteAdapter;
import com.example.fauza.zmdb.data.MovieDetailsContract;
import com.example.fauza.zmdb.data.MovieDetailsDbHelper;
import com.example.fauza.zmdb.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private LinearLayout linearLayoutMainActivity;
    private Toolbar toolbarMain;
    private Spinner spinnerMovieCategories;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewMoviesPosters;
    private RecyclerView recyclerViewMoviesPostersFavorite;
    private MoviePosterAdapter moviePosterAdapter;
    private MoviePosterFavoriteAdapter moviePosterFavoriteAdapter;

    private SQLiteDatabase mDb;
    private MovieDetailsDbHelper mDbHelper;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new MovieDetailsDbHelper(MainActivity.this);
        mDb = mDbHelper.getReadableDatabase();

        linearLayoutMainActivity = findViewById(R.id.linearLayout_activity_main);
        toolbarMain = findViewById(R.id.toolbar_main);
        spinnerMovieCategories = findViewById(R.id.spinner_movie_categories);
        progressBar = findViewById(R.id.progressbar);
        recyclerViewMoviesPosters = findViewById(R.id.recyclerView_movies_posters);
        recyclerViewMoviesPostersFavorite = findViewById(R.id.recyclerView_movies_posters_favorite);

        recyclerViewMoviesPosters.setVisibility(View.INVISIBLE);
        recyclerViewMoviesPostersFavorite.setVisibility(View.INVISIBLE);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                MainActivity.this,
                R.array.movie_categories,
                R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovieCategories.setAdapter(spinnerAdapter);
        spinnerMovieCategories.setOnItemSelectedListener(this);

        moviePosterAdapter = new MoviePosterAdapter(MainActivity.this);
        moviePosterFavoriteAdapter = new MoviePosterFavoriteAdapter(MainActivity.this);
        recyclerViewMoviesPosters.setAdapter(moviePosterAdapter);
        recyclerViewMoviesPostersFavorite.setAdapter(moviePosterFavoriteAdapter);
        recyclerViewMoviesPosters.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        recyclerViewMoviesPostersFavorite.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerViewMoviesPostersFavorite.getVisibility() == View.VISIBLE) {
            moviePosterFavoriteAdapter.setmCursor(getAllFavorite());
            moviePosterFavoriteAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        switch (pos) {
            case 0:
                recyclerViewMoviesPosters.setVisibility(View.INVISIBLE);
                recyclerViewMoviesPostersFavorite.setVisibility(View.INVISIBLE);
                if (!isNetworkOnline()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("You have no internet connection.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    recyclerViewMoviesPosters.setVisibility(View.INVISIBLE);
                    recyclerViewMoviesPostersFavorite.setVisibility(View.INVISIBLE);
                    Uri popularMoviesUri = NetworkUtils.getPopularMovies(MainActivity.this);
                    new NetworkTask().execute(popularMoviesUri);
                    recyclerViewMoviesPosters.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                recyclerViewMoviesPosters.setVisibility(View.INVISIBLE);
                recyclerViewMoviesPostersFavorite.setVisibility(View.INVISIBLE);
                if (!isNetworkOnline()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("You have no internet connection.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    recyclerViewMoviesPosters.setVisibility(View.INVISIBLE);
                    recyclerViewMoviesPostersFavorite.setVisibility(View.INVISIBLE);
                    Uri topRateMoviesUri = NetworkUtils.getTopRatedMovies(MainActivity.this);
                    new NetworkTask().execute(topRateMoviesUri);
                    recyclerViewMoviesPosters.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                recyclerViewMoviesPosters.setVisibility(View.INVISIBLE);
                recyclerViewMoviesPostersFavorite.setVisibility(View.VISIBLE);
                moviePosterFavoriteAdapter.setmCursor(getAllFavorite());
                moviePosterFavoriteAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public ArrayList<String> getMoviesIds(String mJSONData) {
        ArrayList<String> movieIds = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(mJSONData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieResult = jsonArray.getJSONObject(i);
                String mId = movieResult.getString(getString(R.string.movieId));
                movieIds.add(mId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieIds;
    }

    public ArrayList<String> getMoviesTitles(String mJSONData) {
        ArrayList<String> movieTitles = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(mJSONData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieResult = jsonArray.getJSONObject(i);
                String movieTitle = movieResult.getString(getString(R.string.movieTitle));
                movieTitles.add(movieTitle);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieTitles;
    }

    public ArrayList<String> getMoviesPosterPaths(String mJSONData) {
        ArrayList<String> moviePosterPaths = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(mJSONData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieResult = jsonArray.getJSONObject(i);
                String posterPath = movieResult.getString(getString(R.string.moviePosterPath));
                moviePosterPaths.add(posterPath);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviePosterPaths;
    }

    class NetworkTask extends AsyncTask<Uri, Void, String> {

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
            moviePosterAdapter.setId(getMoviesIds(s));
            moviePosterAdapter.setMovieTitles(getMoviesTitles(s));
            moviePosterAdapter.setPosterPath(getMoviesPosterPaths(s));
            moviePosterAdapter.notifyDataSetChanged();
        }
    }

    private Cursor getAllFavorite() {
        return mDb.query(
                MovieDetailsContract.MoviDetailsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
