package com.williammora.openfeed.fragments;

import com.squareup.otto.Subscribe;
import com.williammora.openfeed.events.TwitterEvents;

import twitter4j.Query;

public class PopularSearchResultsFragment extends SearchResultsFragment {

    public static final String TAG = PopularSearchResultsFragment.class.getSimpleName();

    @Override
    protected Query.ResultType getResultType() {
        return Query.ResultType.popular;
    }

    @Subscribe
    public void onSearchEvent(TwitterEvents.SearchEvent event) {
        super.onSearchEvent(event);
    }
}
