package com.udacity.josep.udacityproject1;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import com.udacity.josep.udacityproject1.databinding.ActivityDetailBinding;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private Movie movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Bundle b = getIntent().getExtras();
        movie = b.getParcelable(MainActivity.MOVIE);

        binding.setMovie(movie);




        binding.buttonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
                RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
                Realm.setDefaultConfiguration(realmConfig);

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(movie);
                    }
                });
            }
        });

        downloadReviews();

        downloadTrailers();

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
       /* if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }*/
    }

    private void downloadReviews() {
        new DownloadingTask(this, new DownloadingTask.UpdateDataListener() {
            @Override
            public void dataUpdated(ArrayList<Movie> moviesUpdated) {

            }

            @Override
            public void onTrailersRetrieved(List<DownloadingTask.Trailer> trailers) {
            }

            @Override
            public void onReviewsRetrieved(List<String> reviews) {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                binding.reviewsList.setHasFixedSize(true);

                // use a linear layout manager
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(MovieDetailActivity.this);
                binding.reviewsList.setLayoutManager(mLayoutManager);

                SimpleRVAdapter myRecyclerViewAdapter = new SimpleRVAdapter(reviews);
                binding.reviewsList.setAdapter(myRecyclerViewAdapter);
                binding.reviewsList.getAdapter().notifyDataSetChanged();
            }
        }, DownloadingTask.ACTION.REVIEWS, movie.getId()).execute();
    }

    private void downloadTrailers() {
        new DownloadingTask(this, new DownloadingTask.UpdateDataListener() {
            @Override
            public void dataUpdated(ArrayList<Movie> moviesUpdated) {

            }

            @Override
            public void onTrailersRetrieved(List<DownloadingTask.Trailer> trailers) {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                binding.trailersList.setHasFixedSize(true);

                // use a linear layout manager
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(MovieDetailActivity.this);
                binding.trailersList.setLayoutManager(mLayoutManager);

                List<String> titles = new ArrayList<String>();
                for (int i = 0; i < trailers.size(); i++){
                    titles.add(trailers.get(i).getName());
                }

                SimpleRVAdapter myRecyclerViewAdapter = new SimpleRVAdapter(trailers, titles);
                binding.trailersList.setAdapter(myRecyclerViewAdapter);
                binding.trailersList.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onReviewsRetrieved(List<String> reviews) {

            }
        }, DownloadingTask.ACTION.TRAILERS, movie.getId()).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
