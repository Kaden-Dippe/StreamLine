package org.mobiledevsberkeley.streamline;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by joey on 10/21/17.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private ArrayList<Post> posts;
    private static final Post.PostTimePostedByMostRecentComparator postComparator = new Post.PostTimePostedByMostRecentComparator();

    public ArrayList<Post> getSocials(){
        return posts;
    }

    public PostAdapter(Context context, ArrayList<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        //Add stuff to view.
        final Post post = posts.get(position);
        holder.albumArt.setImageDrawable(null);
        Utils.fetchAlbumArt(context, post.getImageUrl(), holder.albumArt, post);
        holder.songTitle.setText(post.getSongTitle());
        holder.posterUserID.setText(post.getUsername());
        holder.artist.setText(post.getArtist());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SliderActivity) context).setPlayingSong(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void addItem(Post post) {
        if (posts.contains(post)) {
            posts.remove(post);
        }
        posts.add(post);
        //Sorting every time we add a new post WILL NOT SCALE. However, doing it for now because it's easier.
        Collections.sort(posts, postComparator);
        notifyDataSetChanged();
    }

    public void removeItem(Post post) {
        if (posts.contains(post)) {
            posts.remove(post);
        }
        notifyDataSetChanged();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        ImageView albumArt;
        TextView artist;
        TextView posterUserID;

        public PostViewHolder(View v){
            super(v);
            songTitle = v.findViewById(R.id.songTitle);
            albumArt = v.findViewById(R.id.albumArt);
            artist = v.findViewById(R.id.artist);
            posterUserID = v.findViewById(R.id.postUserID);
        }

    }
}
