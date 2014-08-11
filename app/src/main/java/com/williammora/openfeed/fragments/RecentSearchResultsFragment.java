package com.williammora.openfeed.fragments;

import com.squareup.otto.Subscribe;
import com.williammora.openfeed.events.TwitterEvents;

import twitter4j.Query;

public class RecentSearchResultsFragment extends SearchResultsFragment {

    public static final String TAG = RecentSearchResultsFragment.class.getSimpleName();

    @Override
    protected Query.ResultType getResultType() {
        return Query.ResultType.recent;
    }

    @Subscribe
    public void onSearchEvent(TwitterEvents.SearchEvent event) {
        super.onSearchEvent(event);
    }
}
