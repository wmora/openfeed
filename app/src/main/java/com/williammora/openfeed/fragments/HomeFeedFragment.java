package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
import com.williammora.openfeed.activities.StatusActivity;
import com.williammora.openfeed.adapters.FeedAdapter;
import com.williammora.openfeed.dto.UserFeed;
import com.williammora.openfeed.listeners.OnRecyclerViewItemClickListener;
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

public class HomeFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerView.OnScrollListener, OnRecyclerViewItemClickListener<Status> {

    private static String TAG = HomeFeedFragment.class.getSimpleName();

    private static final String SAVED_USER_FEED = "SAVED_USER_FEED";

    public interface HomeFeedFragmentListener {
        public void onRefreshRequested();

        public void onRefreshCompleted();
    }

    private RecyclerView mFeed;
    private FeedAdapter mAdapter;
    private SwipeRefreshLayout mFeedContainer;
    private HomeFeedFragmentListener mListener;
    private Twitter mTwitter;
    private UserFeed mUserFeed;
    private Paging mPaging;
    private boolean mRequestingMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_feed, container, false);

        if (savedInstanceState != null) {
            mUserFeed = (UserFeed) savedInstanceState.getSerializable(SAVED_USER_FEED);
        } else {
            mUserFeed = new UserFeed();
            mUserFeed.setStatuses(new ArrayList<Status>());
        }

        mAdapter = new FeedAdapter(mUserFeed.getStatuses(), getActivity());
        mAdapter.setOnItemClickListener(this);

        mFeed = (RecyclerView) rootView.findViewById(R.id.feed);
        mFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFeed.setAdapter(mAdapter);
        mFeed.setOnScrollListener(this);

        mFeedContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.feed_container);
        mFeedContainer.setColorSchemeColors(getResources().getColor(R.color.openfeed_deep_orange_a400),
                getResources().getColor(R.color.openfeed_deep_orange_a700),
                getResources().getColor(R.color.openfeed_deep_orange_a200),
                getResources().getColor(R.color.openfeed_deep_orange_a100));
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
        if (mUserFeed.getStatuses().isEmpty()) {
            mFeedContainer.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_USER_FEED, mUserFeed);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        mPaging = new Paging();
        mPaging.count(50);
        if (!mUserFeed.getStatuses().isEmpty()) {
            mPaging.setSinceId(mUserFeed.getStatuses().get(0).getId());
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
        mFeedContainer.setEnabled(false);
        mFeedContainer.setRefreshing(true);
    }

    public void onRequestCompleted() {
        mRequestingMore = false;
        mFeedContainer.setRefreshing(false);
        mListener.onRefreshCompleted();
        mFeedContainer.setEnabled(true);
    }

    public void updateFeed(UserFeed userFeed) {

        List<Status> statuses = userFeed.getStatuses();
        Paging paging = userFeed.getPaging();

        if (statuses == null || statuses.isEmpty()) {
            return;
        }

        if (mUserFeed.getStatuses().isEmpty()) {
            // Timeline was empty
            statuses.addAll(mUserFeed.getStatuses());
            mUserFeed.setStatuses(statuses);
            mAdapter.setDataset(mUserFeed.getStatuses());
        } else if (paging.getMaxId() > 1) {
            // Previous statuses were requested
            statuses.remove(0);
            mUserFeed.getStatuses().addAll(statuses);
            mAdapter.addAll(statuses);
        } else {
            // Latest statuses were requested
            mUserFeed.getStatuses().addAll(0, statuses);
            mAdapter.addAll(0, statuses);
            mFeed.smoothScrollToPosition(0);
        }

        mUserFeed.setPaging(paging);
    }

    public void onScrollStateChanged(int i) {
    }

    public void onScrolled(int x, int y) {
        if (mRequestingMore) {
            return;
        }
        if (feedBottomReached()) {
            requestPreviousStatuses();
        }
    }

    private void requestPreviousStatuses() {
        mPaging = new Paging();
        mPaging.setMaxId(mUserFeed.getStatuses().get(mUserFeed.getStatuses().size() - 1).getId());
        mPaging.count(100);
        requestMore(mPaging);
    }

    private boolean feedBottomReached() {
        // Since we assigned the Status as the View tag we need to compare whatever is last
        // on screen vs the last element on the status list
        View bottomChildView = mFeed.getChildAt(mFeed.getChildCount() - 1);
        if (bottomChildView == null) {
            return false;
        }
        Status currentBottomStatus = (Status) bottomChildView.getTag();
        Status lastStatus = mUserFeed.getStatuses().get(mUserFeed.getStatuses().size() - 1);
        if (lastStatus.isRetweet()) {
            lastStatus = lastStatus.getRetweetedStatus();
        }
        return currentBottomStatus == lastStatus;
    }

    @Override
    public void onItemClick(View view, Status status) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), StatusActivity.class);
        intent.putExtra(StatusActivity.EXTRA_STATUS, status);
        startActivity(intent);
    }

    private class HomeFeedTask extends AsyncTask<Paging, Void, UserFeed> {

        @Override
        protected UserFeed doInBackground(Paging... pagings) {
            try {
                List<twitter4j.Status> statuses = mTwitter.getHomeTimeline(pagings[0]);
                UserFeed userFeed = new UserFeed();
                userFeed.setPaging(pagings[0]);
                userFeed.setStatuses(statuses);
                return userFeed;
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
        protected void onPostExecute(UserFeed userFeed) {
            super.onPostExecute(userFeed);
            if (userFeed != null) {
                updateFeed(userFeed);
            }
            onRequestCompleted();
        }
    }
}
