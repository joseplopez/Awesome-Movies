package com.udacity.josep.udacityproject1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.squareup.picasso.Picasso;
import com.udacity.josep.udacityproject1.databinding.ListItemBinding;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ArrayList<Movie> movies;
    private GridView gridView;
    public static final String MOVIE = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        movies = new ArrayList<>();

        gridView = (GridView) findViewById(R.id.movie_list);
        assert gridView != null;
        setupGridView(gridView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Send intent to SingleViewActivity

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                   // arguments.putString(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    arguments.putParcelable(MOVIE, movies.get(position));
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    //intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    intent.putExtra(MOVIE, movies.get(position));

                    context.startActivity(intent);
                }


            }
        });
    }

    private void setupGridView(@NonNull GridView gridView) {
        gridView.setAdapter(new MovieAdapter(getBaseContext(), movies));
    }

    public class MovieAdapter extends BaseAdapter {
        private Context mContext;

        public void setMovies(ArrayList<Movie> movies) {
            this.movies = movies;
        }

        private ArrayList<Movie> movies;

        // Constructor
        public MovieAdapter(Context c, ArrayList<Movie> items) {
            this.mContext = c;
            this.movies = items;
        }

        public int getCount() {
            return movies.size();
        }


        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItemBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.list_item, parent, false);
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + movies.get(position).getImageThumbnail()).into(binding.imageView);
            return binding.getRoot();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        updateData();
    }

    private void updateData() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        int downloadType = Integer.valueOf(SP.getString(this.getString(R.string.sort_type),this.getString(R.string.sort_order_default_value)));
        if(downloadType == 0 || downloadType ==1) {
            if (isOnline()) {
                new DownloadingTask(this, new DownloadingTask.UpdateDataListener() {
                    @Override
                    public void dataUpdated(ArrayList<Movie> moviesUpdated) {
                        movies = moviesUpdated;
                        ((MovieAdapter) gridView.getAdapter()).setMovies(movies);
                        ((MovieAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    }

                    @Override
                    public void onTrailersRetrieved(List<DownloadingTask.Trailer> trailers) {

                    }

                    @Override
                    public void onReviewsRetrieved(List<String> trailers) {

                    }
                }, DownloadingTask.ACTION.FILMS_LIST, null).execute();
            }
        }else if(downloadType == 2){
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
            Realm.setDefaultConfiguration(realmConfig);

            Realm realm = Realm.getDefaultInstance();
            RealmResults<Movie> moviesFavourites = realm.where(Movie.class).findAll();
            ArrayList<Movie> result = new ArrayList<>();
            for(int i = 0; i < moviesFavourites.size(); i++){
                result.add(moviesFavourites.get(i));
            }
            movies = result;
            ((MovieAdapter) gridView.getAdapter()).setMovies(movies);
            ((MovieAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }else if(id == R.id.menu_refresh){
            updateData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
