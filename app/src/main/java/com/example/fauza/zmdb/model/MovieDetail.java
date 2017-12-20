package com.example.fauza.zmdb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetail {
    public String backdrop_path;
    public ArrayList<String> genres;
    public String movieId;
    public String overview;
    public String posterPath;
    public String releaseDate;
    public String movieTitle;
    public String voteAverage;
    public String voteCount;

    public MovieDetail(JSONObject jsonObject) {
        try {
            ArrayList<String> genreNames = new ArrayList<>();
            JSONArray genres = jsonObject.getJSONArray("genres");
            for (int i = 0; i < genres.length(); i++) {
                JSONObject jsonObject1 = genres.getJSONObject(i);
                String genreName = jsonObject1.getString("name");
                genreNames.add(genreName);
            }
            this.backdrop_path = jsonObject.getString("backdrop_path");
            this.genres = genreNames;
            this.movieId = jsonObject.getString("id");
            this.overview = jsonObject.getString("overview");
            this.posterPath = jsonObject.getString("poster_path");
            this.releaseDate = jsonObject.getString("release_date");
            this.movieTitle = jsonObject.getString("title");
            this.voteAverage = jsonObject.getString("vote_average");
            this.voteCount = jsonObject.getString("vote_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
