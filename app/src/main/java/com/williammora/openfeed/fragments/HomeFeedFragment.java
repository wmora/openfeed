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

public class HomeFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnScrollListener {

    private static String TAG = HomeFeedFragment.class.getSimpleName();

    public interface HomeFeedFragmentListener {
        public void onRefreshRequested();

        public void onRefreshCompleted();
    }

    private RecyclerView mFeed;
    private FeedAdapter mAdapter;
    private SwipeRefreshLayout mFeedContainer;
    private HomeFeedFragmentListener mListener;
    private Twitter mTwitter;
    private List<Status> mStatuses;
    private Paging mPaging;
    private boolean mRequestingMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mStatuses = new ArrayList<Status>();
        mAdapter = new FeedAdapter(mStatuses);

        mFeed = (RecyclerView) rootView.findViewById(R.id.feed);
        mFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFeed.setAdapter(mAdapter);
        mFeed.setOnScrollListener(this);

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
    public void onResume() {
        super.onResume();
        if (mStatuses.isEmpty()) {
            mFeedContainer.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        mPaging = new Paging();
        mPaging.count(50);
        if (!mStatuses.isEmpty()) {
            mPaging.setSinceId(mStatuses.get(0).getId());
        }
        requestMore(mPaging);
    }

    private void requestMore(Paging paging) {
        if (mRequestingMore) {
            return;
        }
        mListener.onRefreshRequested();
        new HomeFeedTask().execute(paging);
        mRequestingMore = true;
    }

    public void onRequestCompleted() {
        mRequestingMore = false;
        mFeedContainer.setRefreshing(false);
        mListener.onRefreshCompleted();
    }

    public void updateStatuses(List<Status> statuses) {
        if (!mStatuses.isEmpty() && !statuses.isEmpty()) {
            if (mStatuses.get(mStatuses.size() - 1).getId() == statuses.get(0).getId()) {
                statuses.remove(0);
                mStatuses.addAll(statuses);
                mAdapter.addAll(statuses);
            }
        } else {
            statuses.addAll(mStatuses);
            mStatuses = statuses;
            mAdapter.setDataset(mStatuses);
        }
    }

    public void onScrollStateChanged(int i) {
    }

    public void onScrolled(int x, int y) {
        if (feedBottomReached()) {
            requestPreviousStatuses();
        }
    }

    private void requestPreviousStatuses() {
        mPaging = new Paging();
        mPaging.setMaxId(mStatuses.get(mStatuses.size() - 1).getId());
        mPaging.count(100);
        requestMore(mPaging);
    }

    private boolean feedBottomReached() {
        // Since we assigned the Status id as the View tag we need to compare whatever is last
        // on screen vs the id of the last element in the status list
        View currentBottomChildView = mFeed.getChildAt(mFeed.getChildCount() - 1);
        long lastStatusId = mStatuses.get(mStatuses.size() - 1).getId();
        return currentBottomChildView.getTag().toString().equals(String.valueOf(lastStatusId));
    }

    private class HomeFeedTask extends AsyncTask<Paging, Void, List<Status>> {

        @Override
        protected List<twitter4j.Status> doInBackground(Paging... pagings) {
            try {
                return mTwitter.getHomeTimeline(mPaging);
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
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            if (statuses != null) {
                updateStatuses(statuses);
            }
            onRequestCompleted();
        }
    }
}
