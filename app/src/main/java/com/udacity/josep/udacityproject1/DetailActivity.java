package com.udacity.josep.udacityproject1;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.josep.udacityproject1.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Bundle b = getIntent().getExtras();
        Movie movie = b.getParcelable(MainActivity.MOVIE);

        binding.setMovie(movie);


    }
}
