package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import okhttp3.Headers;

public class ComposeFragment extends DialogFragment {

    EditText etCompose;
    Button btnTweet;
    TextInputLayout tiTextInputLayout;
    TwitterClient client;
    ImageView ivProfileImage;
    TextView tvUsernameCompose;
    //To enforce tweet max length
    int maxLength;

    public interface OnPostTweetListener {
        public void onTweetPass(Tweet tweet);
    }

    OnPostTweetListener onPostTweetListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        // extract the implementation obj from passed context (from parent activity)
        onPostTweetListener = (OnPostTweetListener) context;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public interface OnTweetActionListener {
        void onFinishTweet(Tweet tweet);
    }

    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance() {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTweet = view.findViewById(R.id.btnTweet);
        tiTextInputLayout = view.findViewById(R.id.tiTextInputLayout);
        etCompose = view.findViewById(R.id.etCompose);
        client = TwitterApplication.getRestClient(getContext());
        ivProfileImage = view.findViewById(R.id.ivProfileCompose);
        //Set up text input counter and limiter
        maxLength = getResources().getInteger(R.integer.tweet_max_length);
        tiTextInputLayout.setCounterMaxLength(maxLength);
        tiTextInputLayout.setCounterEnabled(true);
        tvUsernameCompose = view.findViewById(R.id.tvUsernameCompose);

        tvUsernameCompose.setText(User.getCurrentUser().getScreenName());
        Glide.with(getContext())
                .load(User.getCurrentUser().getProfileImageURL())
                .transform(new CircleCrop())
                .into(ivProfileImage);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.length() < getResources().getInteger(R.integer.tweet_max_length)) {
                    client.postTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            try {
                                onPostTweetListener.onTweetPass(Tweet.fromJson(json.jsonObject));
                                //getActivity().onBackPressed();
                                getFragmentManager().beginTransaction().remove(ComposeFragment.this).commit();
                            } catch (JSONException e) {
                                Log.e("ComposeFragment", "Could not parse posted tweet");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d("ComposeFragment", "Could not publish tweet");
                        }
                    });
                }
            }
        });
    }
}