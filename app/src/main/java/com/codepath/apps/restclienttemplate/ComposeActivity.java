package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class ComposeActivity extends AppCompatActivity {
    EditText etCompose;
    Button btnTweet;
    TextInputLayout textInputLayout;
    private int max_length;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

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
                    Log.d("ComposeActivity", content);
                }
            }
        });
    }
}