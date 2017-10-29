package org.mobiledevsberkeley.streamline;

import android.graphics.Bitmap;

import java.io.Serializable;

import java.util.Comparator;

/**
 * Created by joey on 10/21/17.
 */

public class Post implements Serializable {
    private String artist;
    private String imageUrl;
    private String songTitle;
    private double timePosted;
    private String trackId;
    private String uid;
    private String username;

    private transient Bitmap bitmap;

    public Post(){}

    public Post(String artist, String imageUrl, String songTitle, double timePosted, String trackId, String uid, String username){
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.songTitle = songTitle;
        this.timePosted = timePosted;
        this.trackId = trackId;
        this.uid = uid;
        this.username = username;
    }

    public String getArtist() {
        return artist;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public double getTimePosted() {
        return timePosted;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Post post = (Post) obj;
        return  this.artist.equals(post.artist) &&
                this.imageUrl.equals(post.imageUrl) &&
                this.songTitle.equals(post.songTitle) &&
                this.timePosted == post.timePosted &&
                this.trackId.equals(post.trackId) &&
                this.uid.equals(post.uid) &&
                this.username.equals(post.username);
    }

    @Override
    public int hashCode() {
        //Overriding hashCode because we overrode equals. Doesn't really do much.
        return Integer.valueOf(this.trackId) + Integer.valueOf(this.uid);
    }

    public static class PostTimePostedByMostRecentComparator implements Comparator<Post> {

        @Override
        public int compare(Post t0, Post t1) {
            double timeDiff = t0.getTimePosted() - t1.getTimePosted();
            return (int) -timeDiff; //negative because we want most recent
        }
    }
}