package com.williammora.openfeed.fragments;

import android.os.AsyncTask;
import android.util.Log;

import com.williammora.openfeed.dto.Feed;

import java.util.List;

import twitter4j.Paging;
import twitter4j.TwitterException;

public class HomeFeedFragment extends AbstractFeedFragment {

    public static String TAG = HomeFeedFragment.class.getSimpleName();

    @Override
    protected void doRequest(Paging paging) {
        new HomeFeedTask().execute(paging);
    }

    private class HomeFeedTask extends AsyncTask<Paging, Void, Feed> {

        @Override
        protected Feed doInBackground(Paging... pagings) {
            try {
                List<twitter4j.Status> statuses = mTwitter.getHomeTimeline(pagings[0]);
                Feed feed = new Feed();
                feed.setPaging(pagings[0]);
                feed.setStatuses(statuses);
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
