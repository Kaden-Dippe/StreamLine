package org.mobiledevsberkeley.streamline;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spotify.sdk.android.player.Metadata;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by nzp on 10/21/17.
 *
 * All code related to the Firebase Database.
 */

public class FirebaseUtils {

    static DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");

    public static void addUserToDatabase(String id, String displayName) {
        if (id == null) {
            Log.d("FIREBASE", "Could not add new user to firebase database (no id)");
            return;
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        if (displayName == null) {
            //they don't connect through facebook. use their id, which is the username
            displayName = id;
        } else {
            //they connect through facebook, use their facebook name
        }
        userRef.child(id).setValue(new User("", displayName));
    }


    public static void addPostToDatabase(Track track, Context context){
        //Do firebase stuff: upload all that info to the main feed
        //need to somehow get username: problem is we don't authenticate users through firebase...
        //code is somewhat repettitive, will optomize later
        //formatting artist string

        String artists = "";
        for(ArtistSimple s: track.artists) {
            artists += s.name + ", ";
        }
        if(artists.substring(artists.length()-2, artists.length()).equals(", ")) {
            artists = artists.substring(0,artists.length()-2);
        }

        String username = "";
        String uid = "";
        String trackID = track.id;
        String artist = artists;
        String imageURL = track.album.images.get(0).url;
        String songTitle = track.name;
        double timePosted = 0;
        String id = ref.push().getKey();

        Post post = new  Post(artist, imageURL, songTitle, timePosted, trackID,  uid, username);
        ref.child(id).setValue(post);
        Intent i = new Intent(context, SliderActivity.class);
        context.startActivity(i);
    }


    public static void initFeedValueEventListener(final SliderActivity callingActivity) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/posts");
        long startDate = Utils.oneDayAgo();
        Query query = ref.orderByChild("timePosted").startAt(startDate);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                callingActivity.addPostToAdapter(post);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                callingActivity.removePostFromAdapter(post);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FIREBASE", "Failed to read value", databaseError.toException());
            }
        };
        query.addChildEventListener(childEventListener);
    }
}
