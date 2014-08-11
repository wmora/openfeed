package com.williammora.openfeed.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.williammora.openfeed.R;
import com.williammora.openfeed.events.TwitterEvents;
import com.williammora.openfeed.fragments.WelcomeFragment;
import com.williammora.openfeed.services.TwitterService;
import com.williammora.openfeed.utils.BusProvider;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TwitterService.getInstance().isSignedIn()) {
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
        BusProvider.getInstance().register(this);

        // Check if we are coming back form authorizing Twitter
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(getString(R.string.twitter_callback_url))) {
            handleTwitterCallback(uri);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void handleTwitterCallback(Uri uri) {
        String verifier = uri.getQueryParameter("oauth_verifier");
        TwitterService.getInstance().getOAuthAccessToken(verifier);
    }

    private void goToHome() {
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void onOAuthRequestTokenEvent(TwitterEvents.OAuthRequestTokenEvent event) {
        Uri uri = Uri.parse(event.getResult().getAuthenticationURL());
        startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), 24);
    }

    @Subscribe
    public void onOAuthAccessTokenEvent(TwitterEvents.OAuthAccessTokenEvent event) {
        goToHome();
    }

}
