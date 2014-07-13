package com.williammora.openfeed.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.williammora.openfeed.R;
import com.williammora.openfeed.fragments.UserFragment;

import twitter4j.User;

public class UserActivity extends Activity implements UserFragment.UserFragmentListener {

    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String SAVED_USER = "SAVED_USER";
    public static final String SAVED_SCREEN_NAME = "SAVED_SCREEN_NAME";

    private User mUser;
    private String mScreenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new UserFragment())
                    .commit();
            Intent intent = getIntent();
            mUser = (User) intent.getSerializableExtra(EXTRA_USER);
            if (mUser == null) {
                Uri uri = getIntent().getData();
                if (uri.getHost().equals(getString(R.string.twitter_users_host))) {
                    mScreenName = uri.getLastPathSegment();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_USER, mUser);
        outState.putString(SAVED_SCREEN_NAME, mScreenName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUser = (User) savedInstanceState.getSerializable(SAVED_USER);
        mScreenName = savedInstanceState.getString(SAVED_SCREEN_NAME, "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public User getUser() {
        return mUser;
    }

    @Override
    public String getUserScreenName() {
        return mScreenName;
    }

    @Override
    public void onUserLoaded(User user) {
        setTitle(user.getName());
        mUser = user;
    }
}
