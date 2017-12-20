package com.example.fauza.zmdb.utils;


import android.content.Context;
import android.net.Uri;

import com.example.fauza.zmdb.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    public static String TMDBAPI_BASE_URL = "https://api.themoviedb.org/3/";
    public static String TMBD_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    public static String YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    public static String YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/";


    public static Uri getPopularMovies(Context mContext) {
        return Uri.parse(TMDBAPI_BASE_URL).buildUpon()
                .appendPath(mContext.getString(R.string.movie))
                .appendPath(mContext.getString(R.string.popularMovie))
                .appendQueryParameter(mContext.getString(R.string.APIKeyQuery), mContext.getString(R.string.TMDBAPIKey))
                .build();
    }

    public static Uri getTopRatedMovies(Context mContext) {
        return Uri.parse(TMDBAPI_BASE_URL).buildUpon()
                .appendPath(mContext.getString(R.string.movie))
                .appendPath(mContext.getString(R.string.topRatedMovie))
                .appendQueryParameter(mContext.getString(R.string.APIKeyQuery), mContext.getString(R.string.TMDBAPIKey))
                .build();
    }

    public static Uri getMovieDetails(Context mContext, String movieId) {
        return Uri.parse(TMDBAPI_BASE_URL).buildUpon()
                .appendPath(mContext.getString(R.string.movie))
                .appendPath(movieId)
                .appendQueryParameter(mContext.getString(R.string.APIKeyQuery), mContext.getString(R.string.TMDBAPIKey))
                .build();
    }

    public static Uri getMovieVideos(Context mContext, String movieId) {
        return Uri.parse(TMDBAPI_BASE_URL).buildUpon()
                .appendPath(mContext.getString(R.string.movie))
                .appendPath(movieId)
                .appendPath(mContext.getString(R.string.videos))
                .appendQueryParameter(mContext.getString(R.string.APIKeyQuery), mContext.getString(R.string.TMDBAPIKey))
                .build();
    }

    public static Uri getMovieReviews(Context mContext, String movieId) {
        return Uri.parse(TMDBAPI_BASE_URL).buildUpon()
                .appendPath(mContext.getString(R.string.movie))
                .appendPath(movieId)
                .appendPath(mContext.getString(R.string.TMDBReviewPath))
                .appendQueryParameter(mContext.getString(R.string.APIKeyQuery), mContext.getString(R.string.TMDBAPIKey))
                .build();
    }

    public static Uri getTMDBImage(Context mContext, String posterPath) {
        String imageUri = TMBD_IMAGE_BASE_URL + mContext.getString(R.string.imageRes500) + posterPath;
        return Uri.parse(imageUri);
    }

    public static Uri getYouTubeLink(Context mContext, String queryKey) {
        return Uri.parse(YOUTUBE_VIDEO_BASE_URL).buildUpon()
                .appendPath("watch")
                .appendQueryParameter("v", queryKey)
                .build();
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
