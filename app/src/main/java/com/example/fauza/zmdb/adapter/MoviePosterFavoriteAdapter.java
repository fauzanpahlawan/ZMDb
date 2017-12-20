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
import com.example.fauza.zmdb.activity.MovieDetailFavoriteActivity;
import com.example.fauza.zmdb.data.MovieDetailsContract;
import com.example.fauza.zmdb.utils.NetworkUtils;
import com.example.fauza.zmdb.viewHolder.MoviePosterViewHolder;

public class MoviePosterFavoriteAdapter extends RecyclerView.Adapter<MoviePosterViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public MoviePosterFavoriteAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_movie_poster, parent, false);
        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviePosterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        int movieIdIndex = mCursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_MOVIE_ID
        );

        int movieTitleIndex = mCursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_TITLE
        );

        int posterPathIndex = mCursor.getColumnIndex(
                MovieDetailsContract.MoviDetailsEntry.COLUMN_POSTER_PATH
        );

        String posterPath = mCursor.getString(posterPathIndex);
        String movieId = mCursor.getString(movieIdIndex);
        String movieTitle = mCursor.getString(movieTitleIndex);

        Uri uri = Uri.parse(posterPath);
        holder.getTextViewMovieTitle().setText(movieTitle);
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

        final Intent intent = new Intent(mContext, MovieDetailFavoriteActivity.class);
        intent.putExtra(mContext.getString(R.string.movieId), movieId);
        holder.getImageViewMoviePoster().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }
}
