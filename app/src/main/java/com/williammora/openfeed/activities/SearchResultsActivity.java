package com.williammora.openfeed.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.williammora.openfeed.R;
import com.williammora.openfeed.fragments.PopularSearchResultsFragment;
import com.williammora.openfeed.fragments.SearchResultsFragment;

public class SearchResultsActivity extends Activity implements
        SearchResultsFragment.SearchResultsFragmentListener {

    private static final String SAVED_QUERY = "SAVED_QUERY";
    private static final String SAVED_SHOWING_GO_TO_TOP = "SAVED_SHOWING_GO_TO_TOP";

    private Menu mMenu;
    private boolean mShowingGoToTop;
    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PopularSearchResultsFragment(), PopularSearchResultsFragment.TAG)
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
        outState.putBoolean(SAVED_SHOWING_GO_TO_TOP, mShowingGoToTop);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getString(SAVED_QUERY, "");
        mShowingGoToTop = savedInstanceState.getBoolean(SAVED_SHOWING_GO_TO_TOP, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_results, menu);
        mMenu = menu;
        showGoToTopOption(mShowingGoToTop);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_go_to_top:
                goToTop();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTitle(String query) {
        if (getActionBar() != null) {
            getActionBar().setTitle(query);
        }
    }

    @Override
    public void showGoToTopOption(boolean shouldShow) {
        mMenu.findItem(R.id.action_go_to_top).setVisible(shouldShow);
        mShowingGoToTop = shouldShow;
    }

    @Override
    public String getQuery() {
        return mQuery;
    }

    private void goToTop() {
        PopularSearchResultsFragment fragment = (PopularSearchResultsFragment) getFragmentManager().
                findFragmentByTag(PopularSearchResultsFragment.TAG);
        fragment.goToTop();
    }
}
