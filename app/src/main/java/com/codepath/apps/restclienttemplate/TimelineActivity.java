package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity  implements ComposeFragment.OnPostTweetListener  {

    //Value used to get tweet made by user
    //Passing the tweet using the API would be inefficient
    private static final int REQUEST_CODE = 1337;

    //Logging
    private final String TAG = "TimeLineActivity";

    public TwitterClient client;
    TweetsAdapter adapter;
    RecyclerView rvFeed;
    List<Tweet> tweets;
    ImageButton btnLogout;
    SwipeRefreshLayout swipeRefreshLayout;
    private MenuItem miProgressBar;
    EndlessRecyclerViewScrollListener endlessScrollListener;

    //keeps track of the number of items received since last API call
    //this is done because calls subsequent to the first populateHomeTimeline() call vary in number
    int newItemsSinceLastCall = 0;


    ComposeFragment.OnPostTweetListener tweetPostListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up recycler and it's adapter
        setContentView(R.layout.activity_timeline);
        tweets = new ArrayList<>();
        rvFeed = findViewById(R.id.rvFeed);
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFeed.setLayoutManager(linearLayoutManager);
        rvFeed.setAdapter(adapter);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        //API handle
        client = TwitterApplication.getRestClient(this);

        endlessScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreData(tweets.get(tweets.size() - 1).id);
            }
        };

        rvFeed.addOnScrollListener(endlessScrollListener);
        //Swipe refresh logic
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                populateHomeTimeLine();
                adapter.notifyDataSetChanged();
            }
        });

        populateHomeTimeLine();
        //loadMoreData(Long.MAX_VALUE);
    }


    private void loadMoreData(long maxId) {
        client.getLatestTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> newtweets = Tweet.fromJsonArray(jsonArray);
                    tweets.addAll(newtweets);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
            //get the max_id, which is always at the end of the list
        }, maxId);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Set up action bar
        miProgressBar = menu.findItem(R.id.miProgressBar);
        Log.d(TAG, "Progressbar loaded!");
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        if (miProgressBar !=null)
            miProgressBar.setVisible(true);
    }

    public void hideProgressBar() {
        if (miProgressBar !=null)
            miProgressBar.setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pattern matching for each menu button action
        switch (item.getItemId()) {
            case R.id.compose: {
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment alertDialog = ComposeFragment.newInstance();
                alertDialog.show(fm, "fragment_alert");
                return true;
            }
            case R.id.logout:{
                client.clearAccessToken();
                finish();
                return true;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            adapter.notifyItemChanged(0);
            rvFeed.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeLine() {
        showProgressBar();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Tweet list size: " + String.valueOf(tweets.size()));
                    Log.d(TAG, "new elements: " + String.valueOf(tweets.size() - newItemsSinceLastCall));

                } catch(JSONException e) {
                    Log.e("TimeLineActivity", "Error parsing JSON data: " + e.toString());
                }
                //Stop indicating a process
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Error retrieving tweets: " + String.valueOf(statusCode) + " " + response);
            }
        });
        hideProgressBar();
    }

    @Override
    public void onTweetPass(Tweet tweet) {
        //Receive new tweet made from the ComposeFragment. Insert into container and notify adapter
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);
        rvFeed.smoothScrollToPosition(0);
    }
}