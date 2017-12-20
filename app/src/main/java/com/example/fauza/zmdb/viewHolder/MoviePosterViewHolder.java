package com.example.fauza.zmdb.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fauza.zmdb.R;

public class MoviePosterViewHolder extends RecyclerView.ViewHolder {
    private TextView textViewMovieTitle;
    private ImageView imageViewMoviePoster;

    public MoviePosterViewHolder(View itemView) {
        super(itemView);
        textViewMovieTitle = itemView.findViewById(R.id.textView_movie_title_poster);
        imageViewMoviePoster = itemView.findViewById(R.id.imageView_movie_poster);
    }

    public ImageView getImageViewMoviePoster() {
        return imageViewMoviePoster;
    }

    public TextView getTextViewMovieTitle() {
        return textViewMovieTitle;
    }

    public void setImageViewMoviePoster(ImageView imageViewMoviePoster) {
        this.imageViewMoviePoster = imageViewMoviePoster;
    }
}
