package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.williammora.openfeed.dto.Feed;
import com.williammora.openfeed.listeners.FeedFragmentListener;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;

public class SearchResultsFragment extends AbstractFeedFragment {

    public static final String TAG = SearchResultsFragment.class.getSimpleName();

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
        new SearchTask().execute(query);
    }

    private class SearchTask extends AsyncTask<Query, Void, Feed> {

        @Override
        protected Feed doInBackground(Query... queries) {
            try {
                Query query = queries[0];
                QueryResult queryResult = mTwitter.search(queries[0]);

                Paging paging = new Paging();
                paging.setCount(query.getCount());
                if (query.getMaxId() > 0) {
                    paging.setMaxId(query.getMaxId());
                }
                if (query.getSinceId() > 1) {
                    paging.setSinceId(query.getSinceId());
                }

                Feed feed = new Feed();
                feed.setPaging(paging);
                feed.setStatuses(queryResult.getTweets());
                return feed;
            } catch (TwitterException e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            onRequestCompleted();
        }

        @Override
        protected void onPostExecute(Feed feed) {
            super.onPostExecute(feed);
            if (feed != null) {
                updateFeed(feed);
            }
            onRequestCompleted();
        }
    }
}
