package com.codepath.apps.restclienttemplate.adapters;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;


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
            Glide.with(context)
                    .load(tweet.getAuthor().getProfileImageURL())
                    .transform(new CircleCrop())
                    .into(ivProfileImage);

            if (tweet.getMediaUrl() != null) {
                Glide.with(context)
                        .load(tweet.getMediaUrl())
                        .transform(new RoundedCorners(30))
                        .into(ivMedia);
                ivMedia.setVisibility(View.VISIBLE);
            }

            tvTimeDelta.setText(getTimeDelta(tweet.getCreatedAt()));

        }
    }

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void adAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
    private String getTimeDelta(String timeStamp) {
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
