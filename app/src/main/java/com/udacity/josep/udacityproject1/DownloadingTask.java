package com.udacity.josep.udacityproject1;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jple on 23/07/16.
 *
 */
public class DownloadingTask extends AsyncTask<URL, Void, ArrayList<Movie>> {


    private ArrayList<String> reviews;
    private ArrayList<Trailer> trailers;

    public interface UpdateDataListener{
        void dataUpdated(ArrayList<Movie> movies);
        void onTrailersRetrieved(List<Trailer> trailers);
        void onReviewsRetrieved(List<String> trailers);
    }

    private final ACTION action;
    private final String id;

    public enum ACTION{
        FILMS_LIST, TRAILERS, REVIEWS
    }

    public static final String RESULTS = "results";
    private Context context;
    private UpdateDataListener updateDataListener;
    private String LOG_TAG = "Download Activity";

    public DownloadingTask(Context context, UpdateDataListener updateDataListener, ACTION action, String id) {
        this.context = context;
        this.updateDataListener = updateDataListener;
        this.action = action;
        this.id = id;
    }

    protected ArrayList<Movie> doInBackground(URL... urls) {



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String resultJsonStr = null;
        String MOVIES_URL = "";
        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            switch (action){
                case FILMS_LIST:
                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
                    int downloadType = Integer.valueOf(SP.getString(context.getString(R.string.sort_type),context.getString(R.string.sort_order_default_value)));

                    String sortOrder;
                    switch (downloadType){
                        case 1:
                            sortOrder = "top_rated";
                            break;
                        case 0:
                        default:
                            sortOrder = "popular";
                            break;

                    }
                    MOVIES_URL = "https://api.themoviedb.org/3/movie/" + sortOrder + "?";
                    break;
                case REVIEWS:
                    MOVIES_URL = "https://api.themoviedb.org/3/movie/" + id + "/reviews";
                    break;
                case TRAILERS:
                    MOVIES_URL = "https://api.themoviedb.org/3/movie/" + id + "/videos";
                    break;
                default:
                    break;
            }

            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            resultJsonStr = buffer.toString();
            switch (action) {
                case FILMS_LIST:
                    return getMoviesDataFromJson(resultJsonStr);
                case REVIEWS:
                    this.reviews = getReviewsDataFromJson(resultJsonStr);
                    return null;
                case TRAILERS:
                    this.trailers = getTrailersDataFromJson(resultJsonStr);
                    return null;

            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

        ArrayList<Movie> moviesResult = new ArrayList<>();
        JSONArray movies = new JSONArray( new JSONObject(moviesJsonStr).getString(RESULTS));

        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            moviesResult.add(new Movie(
                    movie.getString("id"),
                    movie.getString("original_title"),
                    movie.getString("backdrop_path"),
                    movie.getString("overview"),
                    movie.getString("vote_average"),
                    movie.getString("release_date")
            ));
        }

        return moviesResult;
    }

    private ArrayList<String> getReviewsDataFromJson(String moviesJsonStr) throws JSONException {

        ArrayList<String> result = new ArrayList<>();
        JSONArray movies = new JSONArray( new JSONObject(moviesJsonStr).getString(RESULTS));

        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            result.add(movie.getString("content"));
        }

        return result;
    }

    private ArrayList<Trailer> getTrailersDataFromJson(String moviesJsonStr) throws JSONException {

        ArrayList<Trailer> result = new ArrayList<>();
        JSONArray movies = new JSONArray( new JSONObject(moviesJsonStr).getString(RESULTS));

        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            result.add(new Trailer(movie.getString("key"), movie.getString("name")));
        }

        return result;
    }


    protected void onPostExecute(ArrayList<Movie> result) {
        if(updateDataListener!=null && result!=null && result.size()>0){
            updateDataListener.dataUpdated(result);
        }else if(updateDataListener!=null && action== ACTION.REVIEWS){
            updateDataListener.onReviewsRetrieved(reviews);
        }else if(updateDataListener!=null && action== ACTION.TRAILERS){
            updateDataListener.onTrailersRetrieved(trailers);
        }
    }

    public class Trailer{
        public Trailer(String key, String name) {
            this.key = key;
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return "https://www.youtube.com/watch?v=" + key;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private String key;
        private String name;
        private String url;
    }
}
