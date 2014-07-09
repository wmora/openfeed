package com.williammora.openfeed.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.williammora.openfeed.R;
import com.williammora.openfeed.fragments.SearchResultsFragment;

public class SearchResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SearchResultsFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (uri != null) {
            String query = uri.getLastPathSegment();
            if (uri.getHost().equals(getString(R.string.twitter_hashtag_host))) {
                query = "#" + query;
            }
            updateTitle(query);
        }
    }

    private void updateTitle(String query) {
        if (getActionBar() != null) {
            getActionBar().setTitle(query);
        }
    }
}
