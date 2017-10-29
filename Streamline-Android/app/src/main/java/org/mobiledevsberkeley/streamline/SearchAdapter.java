package org.mobiledevsberkeley.streamline;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;


/**
 * Created by kadendippe on 10/27/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomViewHolder> {

    List<Track> results;
    Context context;



    SearchAdapter (List<Track> results, Context context) {
        this.results = results;
        this.context = context;
    }

    public SearchAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view,parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {

        final Track track = results.get(position);
        holder.name.setText(track.name);
        String a = "";
        for(ArtistSimple s: track.artists) {
            a += s.name + ", ";
        }
        if(a.substring(a.length()-2, a.length()).equals(", ")) {
            a = a.substring(0,a.length()-2);
        }
        holder.artist.setText(a);
        holder.album.setText(track.album.name);
        final String url = track.album.images.get(0).url;

    }

    public int getItemCount() {
        if(results == null) {
            return 0;
        }

        return results.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArt;
        TextView name;
        TextView artist;
        TextView album;
        TextView hack;

        public CustomViewHolder(View view) {
            super(view);

            albumArt = (ImageView) view.findViewById(R.id.albumArt);
            name = (TextView) view.findViewById(R.id.songTitle);
            artist = (TextView) view.findViewById(R.id.artist);
            album = (TextView) view.findViewById(R.id.songAlbum);
            hack = (TextView) view.findViewById(R.id.hack);

            view.setOnClickListener(new View.OnClickListener() {

                @Override public void onClick(View v) {


                    FirebaseUtils.addPostToDatabase(results.get(getAdapterPosition()), context);
                }
            });
        }
    }


}
