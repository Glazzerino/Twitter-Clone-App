package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.github.scribejava.apis.TwitterApi;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    EditText etCompose;
    Button btnTweet;
    TextInputLayout textInputLayout;
    int max_length;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApplication.getRestClient(this);
        max_length = getResources().getInteger(R.integer.tweet_max_length);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        textInputLayout = findViewById(R.id.tiTextInputLayout);

        textInputLayout.setCounterEnabled(true);
        //140
        textInputLayout.setCounterMaxLength(max_length);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = etCompose.getText().toString();
                if (content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.empty_tweet_msg), Toast.LENGTH_SHORT).show();
                    return;
                } else if (content.length() > max_length) {
                    //etCompose
                    Toast.makeText(getApplicationContext(), "Limit character exceeded: " + String.valueOf(content.length()), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    client.postTweet(content, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Log.d("ComposeActivity", "onSuccess. Tweet content: " + tweet.getBody());
                                Intent intent = new Intent();
                                intent.putExtra("tweet", Parcels.wrap(tweet));
                                setResult(RESULT_OK, intent);
                            } catch(JSONException e) {
                                Log.e("ComposeActivity", "Tweet Model .fromJson(); Could not parse published tweet: " + e.toString());
                            }

                            finish();
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("ComposeActivity", "onFailure");
                        }
                    });
                }
            }
        });
    }
}