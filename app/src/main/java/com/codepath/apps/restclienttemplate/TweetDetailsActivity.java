package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    ImageView ivMediaDet;
    ImageView ivProfileDet;
    TextView tvUsernameDet;
    TextView tvBodyDet;
    TextView tvTimeDeltaDet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        ivMediaDet = findViewById(R.id.ivMediaDet);
        ivProfileDet = findViewById(R.id.ivProfileDet);
        tvBodyDet = findViewById(R.id.tvBodyDet);
        tvUsernameDet = findViewById(R.id.tvUsernameDet);
        tvTimeDeltaDet = findViewById(R.id.tvTimeDeltaDet);
        ivMediaDet.setVisibility(View.GONE);

//      View population
        Tweet tweet = Parcels.unwrap((getIntent().getParcelableExtra("tweet")));
        tvUsernameDet.setText(tweet.getAuthor().getScreenName());
        tvBodyDet.setText(tweet.getBody());
        tvTimeDeltaDet.setText(TweetsAdapter.getTimeDelta(tweet.getCreatedAt()));

        if (tweet.getMediaUrl().length() != 0) {
            Glide.with(this)
                    .load(tweet.getMediaUrl())
                    .transform(new RoundedCorners(15))
                    .into(ivMediaDet);
            ivMediaDet.setVisibility(View.VISIBLE);
        }

        Glide.with(this)
                .load(tweet.getAuthor().getProfileImageURL())
                .transform(new CircleCrop())
                .into(ivProfileDet);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ivMediaDet.setVisibility(View.GONE);
                finishAfterTransition();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}