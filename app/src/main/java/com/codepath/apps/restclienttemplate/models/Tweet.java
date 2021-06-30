package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
     String body;
     String createdAt;
     User author;
     String timeStampRaw;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        Log.d("Tweet", jsonObject.toString());
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.author = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.timeStampRaw = jsonObject.getString("created_at");

        if (jsonObject.getJSONObject("entities").has("media")) {
            JSONArray mediaUrls = jsonObject.getJSONObject("entities").getJSONArray("media");
            for (int i=0;i<mediaUrls.length(); i++) {
                Log.d("MediaTweet", mediaUrls.get(i).toString());
            }
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getAuthor() {
        return author;
    }
}
