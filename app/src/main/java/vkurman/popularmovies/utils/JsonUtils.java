package vkurman.popularmovies.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vkurman.popularmovies.models.Movie;

/**
 * Created by Vassili Kurman on 05/03/2018.
 */
public class JsonUtils {

    private static final String TAG = "JsonUtils";

    // Results Json name
    private static final String JSON_RESULTS = "results";

    // Movie fields Json names
    private static final String JSON_TITLE = "title";
    private static final String JSON_VOTE_AVERAGE = "vote_average";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_PLOT_SYNOSIS = "overview";

    /**
     * Fetches and returns list of movies from json string.
     *
     * @param json
     * @return List<Movie>
     */
    public static List<Movie> parseMovieJson(String json) {

        final List<Movie> movies = new ArrayList<>();

        try {
            // Parsing json string to json object
            JSONObject jsonObject = new JSONObject(json);
            // Getting json array results from json object
            JSONArray resultsArray = jsonObject.optJSONArray(JSON_RESULTS);
            if(resultsArray.length() > 0) {
                Log.d(TAG, "Objects in json results array: " + resultsArray.length());
                Movie movie = null;
                for(int i = 0; i < resultsArray.length(); i++) {
                    movie = new Movie();

                    JSONObject movieJsonObject = resultsArray.optJSONObject(i);
                    // Getting individual values from json object
                    movie.setTitle(movieJsonObject.optString(JSON_TITLE));
                    movie.setMoviePoster(movieJsonObject.optString(JSON_POSTER_PATH));
                    movie.setVoteAverage(String.valueOf(movieJsonObject.optDouble(JSON_VOTE_AVERAGE)));
                    movie.setReleaseDate(movieJsonObject.optString(JSON_RELEASE_DATE));
                    movie.setPlotSynopsis(movieJsonObject.optString(JSON_PLOT_SYNOSIS));

                    movies.add(movie);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parse Movie Json: " + e);
        }

        return movies;
    }
}
