package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.williammora.openfeed.R;
import com.williammora.openfeed.adapters.FeedAdapter;
import com.williammora.openfeed.services.TwitterService;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class HomeFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = HomeFeedFragment.class.getSimpleName();

    public interface HomeFeedFragmentListener {
        public void onRefreshRequested();

        public void onRefreshCompleted();
    }

    private FeedAdapter mAdapter;
    private SwipeRefreshLayout mFeedContainer;
    private HomeFeedFragmentListener mListener;
    private Twitter mTwitter;
    private List<Status> mStatuses;
    private Paging mPaging;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mStatuses = new ArrayList<Status>();
        mAdapter = new FeedAdapter(mStatuses);
        mPaging = new Paging();

        RecyclerView mFeed = (RecyclerView) rootView.findViewById(R.id.feed);
        mFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFeed.setAdapter(mAdapter);

        mFeedContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.feed_container);
        mFeedContainer.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof HomeFeedFragmentListener) {
            mListener = (HomeFeedFragmentListener) activity;
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(getString(R.string.twitter_oauth_key));
            builder.setOAuthConsumerSecret(getString(R.string.twitter_oauth_secret));
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            mTwitter = factory.getInstance();
            mTwitter.setOAuthAccessToken(TwitterService.getInstance().getAccessToken(activity));
        }
        super.onAttach(activity);
    }

    @Override
    public void onRefresh() {
        if (!mStatuses.isEmpty()) {
            mPaging.setSinceId(mStatuses.get(0).getId());
        }
        requestMore(mPaging);
    }

    private void requestMore(Paging paging) {
        mListener.onRefreshRequested();
        new HomeFeedTask().execute(paging);
    }

    public void onRequestCompleted() {
        mFeedContainer.setRefreshing(false);
        mListener.onRefreshCompleted();
    }

    public void updateStatuses(List<Status> statuses) {
        statuses.addAll(mStatuses);
        mAdapter.setDataset(statuses);
    }

    private class HomeFeedTask extends AsyncTask<Paging, Void, List<Status>> {

        @Override
        protected List<twitter4j.Status> doInBackground(Paging... pagings) {
            try {
                return mTwitter.getHomeTimeline(mPaging);
            } catch (TwitterException e) {
                Log.e(TAG, e.getMessage(), e);
                onRequestCompleted();
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            onRequestCompleted();
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            updateStatuses(statuses);
            onRequestCompleted();
        }
    }
}
