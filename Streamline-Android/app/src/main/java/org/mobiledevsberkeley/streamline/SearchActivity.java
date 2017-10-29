package org.mobiledevsberkeley.streamline;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;

import static org.mobiledevsberkeley.streamline.R.id.search;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    static List<Track> results;
    SearchView search;
    Activity a;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    static private SearchAdapter adapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        a = SearchActivity.this;
        search = findViewById(R.id.search);
        search.setOnQueryTextListener(this);
        results = new ArrayList<>();
        context = this.getApplicationContext();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new SearchAdapter(results, context);
        mRecyclerView.setAdapter(adapter);
    }

    //to get results from search query
    public void getTracks(String text) {
        SpotifyUtils.searchFetcher S = new SpotifyUtils.searchFetcher(text, adapter, mRecyclerView);
        S.execute();
    }


    @Override
    public boolean onQueryTextChange(String s) {
        if (s .equals("") || s == null || s.equals("/b")) {
            return false;
        }
        getTracks(s);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }


}
