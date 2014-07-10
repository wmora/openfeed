package com.williammora.openfeed.fragments;

import twitter4j.Query;

public class PopularSearchResultsFragment extends SearchResultsFragment {

    public static final String TAG = PopularSearchResultsFragment.class.getSimpleName();

    @Override
    protected Query.ResultType getResultType() {
        return Query.ResultType.popular;
    }
}
