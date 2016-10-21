package com.udacity.josep.udacityproject1;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jple on 23/07/16.
 *
 */
public class Movie extends RealmObject implements Parcelable {
    public Movie(String id, String originalTitle, String imageThumbnail, String synopsis, String userRating, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.imageThumbnail = imageThumbnail;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }
    private String originalTitle;
    private String  imageThumbnail;

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PrimaryKey
    public String id;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    private String synopsis;// (called overview in the api)
    private String userRating;// (called vote_average in the api)
    private String releaseDate;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeString(id);
        dest.writeString(originalTitle);
        dest.writeString(imageThumbnail);
        dest.writeString(synopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);

    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        id = in.readString();
        originalTitle = in.readString();
        imageThumbnail = in.readString();
        synopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    public Movie(Parcel in) {
        readFromParcel(in);
    }

    @BindingAdapter({"bind:imageThumbnail"})
    public static void loadImage(ImageView view, String url) {
        Picasso.with(view.getContext()).load("http://image.tmdb.org/t/p/w185/" + url).into(view);
    }
}
