package com.udacity.josep.udacityproject1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;
import com.udacity.josep.udacityproject1.databinding.ActivityMainBinding;
import com.udacity.josep.udacityproject1.databinding.ListItemBinding;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static final String MOVIE = "movie";
    private ActivityMainBinding binding;
    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        movies = new ArrayList<>();

        binding.gridview.setAdapter(new ImageAdapter(this, movies));

        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Send intent to SingleViewActivity
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);

                i.putExtra(MOVIE, movies.get(position));

                startActivity(i);
            }
        });

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
                binding.swiperefresh.setRefreshing(false);
            }
        });
        /*binding.swiperefresh.setColorSchemeColors(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/

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
                        ((ImageAdapter) binding.gridview.getAdapter()).setMovies(movies);
                        ((ImageAdapter) binding.gridview.getAdapter()).notifyDataSetChanged();
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
            ((ImageAdapter) binding.gridview.getAdapter()).setMovies(movies);
            ((ImageAdapter) binding.gridview.getAdapter()).notifyDataSetChanged();
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


    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public void setMovies(ArrayList<Movie> movies) {
            this.movies = movies;
        }

        private ArrayList<Movie> movies;

        // Constructor
        public ImageAdapter(Context c, ArrayList<Movie> items) {
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



}
