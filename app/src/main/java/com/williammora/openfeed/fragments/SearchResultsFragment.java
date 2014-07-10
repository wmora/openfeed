package com.williammora.openfeed.fragments;

import android.os.AsyncTask;

import com.williammora.openfeed.dto.Feed;

import twitter4j.Paging;
import twitter4j.Query;

public class SearchResultsFragment extends AbstractFeedFragment {

    public static final String TAG = SearchResultsFragment.class.getSimpleName();

    @Override
    protected void doRequest(Paging paging) {

    }

    private class SearchTask extends AsyncTask<Query, Void, Feed> {

        @Override
        protected Feed doInBackground(Query... queries) {
            return null;
        }
    }
}
