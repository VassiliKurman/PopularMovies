package vkurman.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies.models.Movie;
import vkurman.popularmovies.utils.MovieUtils;

/**
 * Created by Vassili Kurman on 25/02/2018.
 * Version 1.0
 */
public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickListener {

    private static final String TAG = "MainActivity";
    private static final String SORTING_KEY = "sort";

    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private MoviesQueryTask moviesQueryTask;
    private int sortingId = -1;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Binding views
        ButterKnife.bind(this);

        // use a grid layout manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState != null) {
            sortingId = savedInstanceState.getInt(SORTING_KEY);
        }

        moviesQueryTask = new MoviesQueryTask();
        moviesQueryTask.execute(
            (sortingId == -1 || sortingId == R.id.popular) ?
                MovieUtils.createPopularMovieUrl()
                : MovieUtils.createTopRatedMovieUrl());

        // specifying an adapter and passing empty list at start
        mAdapter = new MoviesAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORTING_KEY, sortingId);
    }

    /**
     * This is where callback is received from
     * {@link vkurman.popularmovies.MoviesAdapter.MovieClickListener}
     *
     * This callback is invoked when item in the list is clicked.
     *
     * @param movie - Sensor in the list of the item that was clicked.
     */
    @Override
    public void onMovieClicked(Movie movie) {
        if(movie != null) {
            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
            intent.putExtra(getString(R.string.extra_poster), movie.getMoviePoster());
            intent.putExtra(getString(R.string.extra_title), movie.getTitle());
            intent.putExtra(getString(R.string.extra_release_date), movie.getReleaseDate());
            intent.putExtra(getString(R.string.extra_vote_average), movie.getVoteAverage());
            intent.putExtra(getString(R.string.extra_plot_synopsis), movie.getPlotSynopsis());
            startActivity(intent);
        } else {
            if(mToast != null) {
                mToast.cancel();
            }

            mToast = Toast.makeText(this, "Movie not set!", Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if (sortingId == -1){
            return true;
        }
        MenuItem item;
        switch (sortingId){
            case R.id.popular:
                item = menu.findItem(R.id.popular);
                item.setChecked(true);
                break;
            case R.id.top_rate:
                item = menu.findItem(R.id.top_rate);
                item.setChecked(true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                item.setChecked(true);
                sortingId = item.getItemId();
                moviesQueryTask = new MoviesQueryTask();
                moviesQueryTask.execute(MovieUtils.createPopularMovieUrl());
                if(mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(this, "popular selected", Toast.LENGTH_LONG);
                mToast.show();

                return true;
            case R.id.top_rate:
                item.setChecked(true);
                sortingId = item.getItemId();
                moviesQueryTask = new MoviesQueryTask();
                moviesQueryTask.execute(MovieUtils.createTopRatedMovieUrl());
                if(mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(this, "top rated selected", Toast.LENGTH_LONG);
                mToast.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MoviesQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String result = null;
            try {
                result = MovieUtils.getResponseFromTheMovieDB(searchUrl);
            } catch(IOException e) {
                Log.e(TAG, "Error response from server");
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s == null) {
                return;
            }
            List<Movie> movies = MovieUtils.parseJson(s);
            // updating adapter
            ((MoviesAdapter) mAdapter).updateMovies(movies);
        }
    }
}
