package com.codepath.apps.restclienttemplate.adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TweetDetailsActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    List<Tweet> tweets;
    Context context;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBody;
        TextView tvUsername;
        ImageView ivProfileImage;
        TextView tvTimeDelta;
        ImageView ivMedia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfileImage = itemView.findViewById(R.id.ivProfile);
            tvTimeDelta = itemView.findViewById(R.id.tvTimeDelta);
            ivMedia = itemView.findViewById(R.id.ivMedia);

            ivMedia.setVisibility(View.GONE);

        }

        public void bind(Tweet tweet) {
            tvUsername.setText(tweet.getAuthor().getScreenName());
            tvBody.setText(tweet.getBody());
            tvTimeDelta.setText(getTimeDelta(tweet.getCreatedAt()));

            Glide.with(context)
                    .load(tweet.getAuthor().getProfileImageURL())
                    .transform(new CircleCrop())
                    .into(ivProfileImage);

            if (tweet.getMediaUrl().length() != 0) {

                Glide.with(context)
                        .load(tweet.getMediaUrl())
                        .transform(new RoundedCorners(30))
                        .into(this.ivMedia);
                ivMedia.setVisibility(View.VISIBLE);
                Log.d("TweetsAdapter", "Image on tweet from: " + tweet.getAuthor().getScreenName());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Tweet clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,TweetDetailsActivity.class);

                    // intent + shared element animations
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    Pair<View, String> p1 = Pair.create((View)ivProfileImage, "profile");
                    Pair<View, String> p2 = Pair.create((View)tvBody, "body");
                    Pair<View, String> p3 = Pair.create((View)tvUsername, "username");
                    Pair<View, String> p4 = Pair.create((View)tvTimeDelta, "time_delta");

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, p1, p2, p3, p4);
                    context.startActivity(intent, options.toBundle());
                }
            });
        }
    }

    //Prevents weird image from incorrectly being displayed on unrelated ViewHolder
    @Override
    public void onViewRecycled(@NonNull @NotNull ViewHolder holder) {
        holder.ivMedia.setVisibility(View.GONE);
        holder.ivMedia.setImageResource(0);
        super.onViewRecycled(holder);
    }

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void adAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public static String getTimeDelta(String timeStamp) {
       // String delta;
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(timeStamp).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("TwitterAdapter", "Time delta: " + relativeDate);
        return relativeDate;
    }
}
