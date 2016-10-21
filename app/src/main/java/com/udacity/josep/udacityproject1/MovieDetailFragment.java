package com.udacity.josep.udacityproject1;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.josep.udacityproject1.databinding.ActivityDetailBinding;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    private ActivityDetailBinding binding;
    private Movie movie;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_detail, container, false);

        Bundle b = getArguments();
        movie = b.getParcelable(MainActivity.MOVIE);

        binding.setMovie(movie);

        binding.buttonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
                RealmConfiguration realmConfig = new RealmConfiguration.Builder(getActivity()).build();
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

        return binding.getRoot();
    }

    private void downloadReviews() {
        new DownloadingTask(getActivity(), new DownloadingTask.UpdateDataListener() {
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
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                binding.reviewsList.setLayoutManager(mLayoutManager);

                SimpleRVAdapter myRecyclerViewAdapter = new SimpleRVAdapter(reviews);
                binding.reviewsList.setAdapter(myRecyclerViewAdapter);
                binding.reviewsList.getAdapter().notifyDataSetChanged();
            }
        }, DownloadingTask.ACTION.REVIEWS, movie.getId()).execute();
    }

    private void downloadTrailers() {
        new DownloadingTask(getActivity(), new DownloadingTask.UpdateDataListener() {
            @Override
            public void dataUpdated(ArrayList<Movie> moviesUpdated) {

            }

            @Override
            public void onTrailersRetrieved(List<DownloadingTask.Trailer> trailers) {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                binding.trailersList.setHasFixedSize(true);

                // use a linear layout manager
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
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
}
