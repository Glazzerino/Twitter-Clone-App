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
     String mediaUrl;
     public Long id;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        Log.d("Tweet", jsonObject.toString());

        tweet.body = jsonObject.getString("full_text");
        //tweet.body = jsonObject.getString("text");

        tweet.createdAt = jsonObject.getString("created_at");
        tweet.author = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        tweet.mediaUrl = "";
        if (jsonObject.getJSONObject("entities").has("media")) {
            JSONArray mediaUrls = jsonObject.getJSONObject("entities").getJSONArray("media");

            // Twitter API specifies that if there is a photo it will be the first element in the array
            JSONObject media = mediaUrls.getJSONObject(0);
            if (media.getString("type").equals("photo")) {
                tweet.mediaUrl = media.getString("media_url_https");
                Log.d(tweet.getAuthor().name, tweet.mediaUrl);
            }
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        long min = Long.MAX_VALUE;
        int minpos = 0;
        for (int i=0; i<jsonArray.length(); i++) {

            //Delete later and uncomment code below
            JSONObject newTweet = jsonArray.getJSONObject(i);
            long id = newTweet.getInt("id");
            if (id < min) {
                min = id;
                minpos = i;
            }
            tweets.add(fromJson(newTweet));
            //tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        Log.d("Tweet", String.format("Min val: %s on pos: %s", min, String.valueOf(minpos)));
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

    public String getMediaUrl() {
        return mediaUrl;
    }
}
