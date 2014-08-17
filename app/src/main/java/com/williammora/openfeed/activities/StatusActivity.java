package com.williammora.openfeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import com.williammora.openfeed.R;
import com.williammora.openfeed.events.TwitterEvents;
import com.williammora.openfeed.fragments.StatusFragment;

import twitter4j.Status;

public class StatusActivity extends OpenFeedActivity {

    public static final String EXTRA_STATUS = "EXTRA_STATUS";
    private static final String SAVED_STATUS = "SAVED_STATUS";

    private Status mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StatusFragment())
                    .commit();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mStatus = (Status) savedInstanceState.getSerializable(SAVED_STATUS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_STATUS, mStatus);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STATUS, mStatus);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Subscribe
    public void onStatusRetweeted(TwitterEvents.RetweetedStatusEvent event) {
        mStatus = event.getResult();
    }

    @Subscribe
    public void onStatusDestroyed(TwitterEvents.DestroyedStatusEvent event) {
        mStatus = event.getResult();
    }

    @Subscribe
    public void onFavoriteStatusCreated(TwitterEvents.CreatedFavoriteEvent event) {
        mStatus = event.getResult();
    }

    @Subscribe
    public void onFavoriteStatusDestroyed(TwitterEvents.DestroyedFavoriteEvent event) {
        mStatus = event.getResult();
    }
}
