package com.williammora.openfeed.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.williammora.openfeed.R;
import com.williammora.openfeed.fragments.WelcomeFragment;
import com.williammora.openfeed.services.TwitterService;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class WelcomeActivity extends Activity implements WelcomeFragment.WelcomeInterface {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    private AsyncTwitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TwitterService.getInstance().isSignedIn(getApplicationContext())) {
            goToHome();
        }

        setContentView(R.layout.activity_welcome);
        setTitle(R.string.title_activity_welcome);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new WelcomeFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpTwitterService();

        // Check if we are coming back form authorizing Twitter
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(getString(R.string.twitter_callback_url))) {
            handleTwitterCallback(uri);
        }
    }

    private void setUpTwitterService() {
        mTwitter = new AsyncTwitterFactory().getInstance();
        mTwitter.addListener(new WelcomeTwitterListener());
        mTwitter.setOAuthConsumer(getString(R.string.twitter_oauth_key),
                getString(R.string.twitter_oauth_secret));
    }

    private void handleTwitterCallback(Uri uri) {
        String verifier = uri.getQueryParameter("oauth_verifier");
        mTwitter.getOAuthAccessTokenAsync(TwitterService.getInstance().getRequestToken(), verifier);
    }

    @Override
    public void onTwitterSignIn() {
        mTwitter.getOAuthRequestTokenAsync(getString(R.string.twitter_callback_url));
    }

    private void goToHome() {
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private class WelcomeTwitterListener extends TwitterAdapter {
        @Override
        public void gotOAuthRequestToken(RequestToken token) {
            TwitterService.getInstance().setRequestToken(token);
            Uri uri = Uri.parse(token.getAuthenticationURL());
            startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), 24);
        }

        @Override
        public void gotOAuthAccessToken(AccessToken token) {
            Log.d(TAG, token.toString());
            TwitterService.getInstance().saveAccessToken(getApplicationContext(), token);
            goToHome();
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            Log.e(TAG, method.toString(), te);
        }
    }

}
