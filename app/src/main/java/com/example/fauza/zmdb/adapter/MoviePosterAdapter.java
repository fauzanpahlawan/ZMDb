package com.example.fauza.zmdb.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.fauza.zmdb.R;
import com.example.fauza.zmdb.activity.MovieDetailActivity;
import com.example.fauza.zmdb.utils.NetworkUtils;
import com.example.fauza.zmdb.viewHolder.MoviePosterViewHolder;

import java.util.ArrayList;


public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterViewHolder> {
    private Context mContext;
    private ArrayList<String> movieIds;
    private ArrayList<String> movieTitles;
    private ArrayList<String> moviePosterPaths;


    public MoviePosterAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setId(ArrayList<String> movieIds) {
        this.movieIds = movieIds;
    }

    public void setMovieTitles(ArrayList<String> movieTitles) {
        this.movieTitles = movieTitles;
    }

    public void setPosterPath(ArrayList<String> moviePosterPaths) {
        this.moviePosterPaths = moviePosterPaths;
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_movie_poster, parent, false);
        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviePosterViewHolder holder, int position) {
        Uri uri = NetworkUtils.getTMDBImage(mContext, moviePosterPaths.get(position));

        holder.getTextViewMovieTitle().setText(movieTitles.get(position));
        Glide.with(mContext)
                .load(uri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.getTextViewMovieTitle().setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.getTextViewMovieTitle().setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.posterplaceholder))
                .into(holder.getImageViewMoviePoster());

        final Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra(mContext.getString(R.string.movieId), movieIds.get(position));

        holder.getImageViewMoviePoster().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (movieIds == null) {
            return 0;
        } else {
            return movieIds.size();
        }
    }
}
