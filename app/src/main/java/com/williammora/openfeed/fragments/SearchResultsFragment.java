package com.williammora.openfeed.fragments;

import android.app.Activity;

import com.squareup.otto.Subscribe;
import com.williammora.openfeed.dto.Feed;
import com.williammora.openfeed.events.TwitterEvents;
import com.williammora.openfeed.listeners.FeedFragmentListener;
import com.williammora.openfeed.services.TwitterService;

import twitter4j.Paging;
import twitter4j.Query;

public abstract class SearchResultsFragment extends AbstractFeedFragment {

    public interface SearchResultsFragmentListener extends FeedFragmentListener {
        public String getQuery();
    }

    private SearchResultsFragmentListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof SearchResultsFragmentListener)) {
            throw new ClassCastException("Activity must implement SearchResultsFragmentListener");
        }
        mListener = (SearchResultsFragmentListener) activity;
    }

    @Override
    protected void doRequest(Paging paging) {
        Query query = new Query();
        query.setQuery(mListener.getQuery());
        query.setCount(paging.getCount());
        query.setMaxId(paging.getMaxId());
        query.setSinceId(paging.getSinceId());
        query.setResultType(getResultType());
        TwitterService.getInstance().search(query);
    }

    public void onSearchEvent(TwitterEvents.SearchEvent event) {
        Feed feed = new Feed();
        feed.setPaging(mPaging);
        feed.setStatuses(event.getResult().getTweets());
        updateFeed(feed);
        onRequestCompleted();
    }

    protected abstract Query.ResultType getResultType();

}
