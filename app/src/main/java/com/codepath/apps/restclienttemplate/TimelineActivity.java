package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static java.lang.Math.abs;

public class TimelineActivity extends AppCompatActivity {

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

    //Max_id and since_id are used to reduce reduncancy on twitter API calls made to serve the infinite scroll feature
    //For more info check this link: https://developer.twitter.com/en/docs/twitter-api/v1/tweets/timelines/guides/working-with-timelines
    private long maxId = Long.MAX_VALUE;
    private long sinceId = 1;

    //keeps track of the number of items received since last API call
    //this is done because calls subsequent to the first populateHomeTimeline() call vary in number
    int newItemsSinceLastCall = 0;

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
                Toast.makeText(getApplicationContext(), "ENDLESS SCROLLING OMG", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Page: " + String.valueOf(page));
                //populateHomeTimeLine(false);
                loadMoreData();
            }
        };

        rvFeed.addOnScrollListener(endlessScrollListener);
        //Swipe refresh logic
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                maxId = Long.MAX_VALUE;
                sinceId = 1;
                populateHomeTimeLine(true);
                adapter.notifyDataSetChanged();
            }
        });
        populateHomeTimeLine(true);
    }

    private void loadMoreData() {
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
        }, tweets.get(tweets.size() - 1).id);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Set up action bar
        miProgressBar = menu.findItem(R.id.miProgressBar);
        Log.d(TAG, "Progressbar loaded!");
        return super.onPrepareOptionsMenu(menu);
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
                Intent intent = new Intent(this, ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
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

    /**
     * @about set notifySetChanged when not meaning to add additional data, like in endless scrolling
     * @param notifySetChanged if true will notify adapter that entire set changed.
     */
    private void populateHomeTimeLine(boolean notifySetChanged) {
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

    }

}