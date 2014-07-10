package com.williammora.openfeed.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.williammora.openfeed.R;
import com.williammora.openfeed.fragments.SearchResultsFragment;
import com.williammora.openfeed.listeners.FeedFragmentListener;

public class SearchResultsActivity extends Activity implements
        FeedFragmentListener {

    private static final String SAVED_QUERY = "SAVED_QUERY";

    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SearchResultsFragment(), SearchResultsFragment.TAG)
                    .commit();
            Uri uri = getIntent().getData();
            if (uri != null) {
                mQuery = uri.getLastPathSegment();
                if (uri.getHost().equals(getString(R.string.twitter_hashtag_host))) {
                    mQuery = "#" + mQuery;
                }
            }
        }
        updateTitle(mQuery);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_QUERY, mQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getString(SAVED_QUERY, "");
    }

    private void updateTitle(String query) {
        if (getActionBar() != null) {
            getActionBar().setTitle(query);
        }
    }

    @Override
    public void onRefreshRequested() {

    }

    @Override
    public void onRefreshCompleted() {

    }

    @Override
    public void showGoToTopOption(boolean shouldShow) {

    }
}
