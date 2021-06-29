package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private final String TAG = "TimeLineActivity";
    TwitterClient client;
    TweetsAdapter adapter;
    RecyclerView rvFeed;
    List<Tweet> tweets;
    ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        tweets = new ArrayList<>();
        rvFeed = findViewById(R.id.rvFeed);
        adapter = new TweetsAdapter(this, tweets);
        rvFeed.setLayoutManager(new LinearLayoutManager(this));
        rvFeed.setAdapter(adapter);
        //btnLogout = findViewById(R.id.btnLogout);
       // btnLogout.setOnClickListener(new View.OnClickListener() {
           // @Override
         //   public void onClick(View view) {
             //   client.clearAccessToken();
             //   finish();
           // }
       // });

        client = TwitterApplication.getRestClient(this);
        populateHomeTimeLine();
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
                startActivity(intent);
                break;
            }
            case R.id.logout:{
                client.clearAccessToken();
                finish();
                break;
            }
        }
        return true;
    }

    private void populateHomeTimeLine() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    Log.e("TimeLineActivity", "Error parsing JSON data: " + e.toString());
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure", throwable);
                Log.e(TAG, "Error retrieving tweets: " + String.valueOf(statusCode) + " " + response);
                Toast.makeText(getApplicationContext(), "Could not retrieve tweets", Toast.LENGTH_SHORT).show();
            }
        });
    }
}