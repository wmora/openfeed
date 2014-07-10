package com.williammora.openfeed.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.williammora.openfeed.R;
import com.williammora.openfeed.fragments.HomeFeedFragment;
import com.williammora.openfeed.listeners.FeedFragmentListener;
import com.williammora.openfeed.services.TwitterService;

public class HomeActivity extends Activity implements FeedFragmentListener {

    private static final String SAVED_SHOWING_GO_TO_TOP = "SAVED_SHOWING_GO_TO_TOP";

    private Menu mMenu;
    private boolean mShowingGoToTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFeedFragment(), HomeFeedFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVED_SHOWING_GO_TO_TOP, mShowingGoToTop);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mShowingGoToTop = savedInstanceState.getBoolean(SAVED_SHOWING_GO_TO_TOP, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        mMenu = menu;
        showGoToTopOption(mShowingGoToTop);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_go_to_top:
                goToTop();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToTop() {
        HomeFeedFragment fragment = (HomeFeedFragment) getFragmentManager().findFragmentByTag(HomeFeedFragment.TAG);
        fragment.goToTop();
    }

    private void signOut() {
        TwitterService.getInstance().signOut(getApplicationContext());
        goToWelcome();
    }

    private void goToWelcome() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRefreshRequested() {
        setTitle(R.string.loading);
    }

    @Override
    public void onRefreshCompleted() {
        setTitle(R.string.title_activity_home);
    }

    @Override
    public void showGoToTopOption(boolean shouldShow) {
        mMenu.findItem(R.id.action_go_to_top).setVisible(shouldShow);
        mShowingGoToTop = shouldShow;
    }
}
