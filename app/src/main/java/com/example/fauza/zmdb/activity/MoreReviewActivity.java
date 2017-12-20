package com.example.fauza.zmdb.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.fauza.zmdb.R;
import com.example.fauza.zmdb.adapter.MoreReviewAdapter;
import com.example.fauza.zmdb.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MoreReviewActivity extends AppCompatActivity {


    private Toolbar toolbarMain;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MoreReviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_review);
        toolbarMain = findViewById(R.id.toolbar_main);
        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerView_more_review);

        mAdapter = new MoreReviewAdapter(MoreReviewActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MoreReviewActivity.this));
        recyclerView.setAdapter(mAdapter);

        Intent intentFromMovieDetail = getIntent();
        String movieId = intentFromMovieDetail.getStringExtra(getString(R.string.movieId));
        Uri uriMovieReviews = NetworkUtils.getMovieReviews(MoreReviewActivity.this, movieId);
        new NetworkTaskMovieReview().execute(uriMovieReviews);

        toolbarMain.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbarMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public ArrayList<String> getReviews(String jsonData) {
        ArrayList<String> moreReviews = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject result = jsonArray.getJSONObject(i);
                String authorReviewBuilder = result.getString("author") +
                        ": \n\n" +
                        result.getString("content");
                moreReviews.add(authorReviewBuilder);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moreReviews;

    }

    class NetworkTaskMovieReview extends AsyncTask<Uri, Void, String> {
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
            mAdapter.setMoreReviews(getReviews(s));
            mAdapter.notifyDataSetChanged();
        }
    }
}
