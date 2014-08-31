package com.williammora.openfeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import com.squareup.otto.Subscribe;
import com.williammora.openfeed.R;
import com.williammora.openfeed.events.NotificationEvent;
import com.williammora.openfeed.fragments.HomeFeedFragment;
import com.williammora.openfeed.listeners.FeedFragmentListener;
import com.williammora.openfeed.listeners.OnComposeStatusButtonClickListener;
import com.williammora.openfeed.services.TwitterService;
import com.williammora.openfeed.widgets.FloatingActionButton;
import com.williammora.snackbar.Snackbar;

public class HomeActivity extends OpenFeedActivity implements FeedFragmentListener {

    private static final String SAVED_SHOWING_GO_TO_TOP = "SAVED_SHOWING_GO_TO_TOP";

    private Menu mMenu;
    private boolean mShowingGoToTop;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFeedFragment(), HomeFeedFragment.TAG)
                    .commit();
        }
        initFab();
    }

    private void initFab() {
        mFab = new FloatingActionButton.Builder(this)
                .withColor(getResources().getColor(R.color.openfeed_deep_orange_a400))
                .withDrawable(getResources().getDrawable(R.drawable.compose_primary_color_small))
                .withSize(72)
                .withMargins(0, 0, 16, 16)
                .create();
        mFab.setClickable(true);
        mFab.setOnClickListener(new OnComposeStatusButtonClickListener());
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
        TwitterService.getInstance().signOut();
        goToWelcome();
    }

    private void goToWelcome() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showGoToTopOption(boolean shouldShow) {
        mMenu.findItem(R.id.action_go_to_top).setVisible(shouldShow);
        mShowingGoToTop = shouldShow;
    }

    @Override
    public void onScrollStateChanged(int i) {
        if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mFab.isHidden()) {
            mFab.show();
        } else {
            mFab.hide();
        }
    }

    @Subscribe
    public void onNotificationReceived(NotificationEvent event) {
        Snackbar.with(this)
                .text(event.getResult())
                .show(this);
    }

}
