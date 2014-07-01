package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.app.Fragment;
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

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;

public class HomeFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static String TAG = HomeFeedFragment.class.getSimpleName();

    public interface HomeFeedFragmentListener {
        public void onRefreshRequested();

        public void onRefreshCompleted();
    }

    private RecyclerView mFeed;
    private RecyclerView.LayoutManager mLayoutManager;
    private FeedAdapter mAdapter;
    private SwipeRefreshLayout mFeedContainer;
    private HomeFeedFragmentListener mListener;
    private AsyncTwitter mTwitter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new FeedAdapter(new ArrayList<Status>());

        mFeed = (RecyclerView) rootView.findViewById(R.id.feed);
        mFeed.setLayoutManager(mLayoutManager);
        mFeed.setAdapter(mAdapter);

        mFeedContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.feed_container);
        mFeedContainer.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof HomeFeedFragmentListener) {
            mListener = (HomeFeedFragmentListener) activity;
            mTwitter = AsyncTwitterFactory.getSingleton();
            mTwitter.addListener(new HomeTwitterListener());
            mTwitter.setOAuthConsumer(getString(R.string.twitter_oauth_key),
                    getString(R.string.twitter_oauth_secret));
            mTwitter.setOAuthAccessToken(TwitterService.getInstance().getAccessToken(activity));
        }
        super.onAttach(activity);
    }

    @Override
    public void onRefresh() {
        mListener.onRefreshRequested();
        mTwitter.getHomeTimeline();
    }

    public void onRequestCompleted() {
        mFeedContainer.setRefreshing(false);
        mListener.onRefreshCompleted();
    }

    public void updateStatuses(List<Status> statuses) {
        mAdapter.setDataset(statuses);
    }

    private class HomeTwitterListener extends TwitterAdapter {

        @Override
        public void gotHomeTimeline(ResponseList<Status> statuses) {
            Log.d(TAG, statuses.toString());
            updateStatuses(statuses);
            onRequestCompleted();
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            Log.e(TAG, method.toString(), te);
            onRequestCompleted();
        }
    }
}
