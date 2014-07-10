package com.williammora.openfeed.fragments;

import twitter4j.Query;

public class RecentSearchResultsFragment extends SearchResultsFragment {

    public static final String TAG = RecentSearchResultsFragment.class.getSimpleName();

    @Override
    protected Query.ResultType getResultType() {
        return Query.ResultType.recent;
    }
}
