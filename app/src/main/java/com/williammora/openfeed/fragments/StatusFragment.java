package com.williammora.openfeed.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;
import com.williammora.openfeed.R;
import com.williammora.openfeed.activities.StatusActivity;
import com.williammora.openfeed.adapters.viewholders.StatusViewHolder;
import com.williammora.openfeed.events.TwitterEvents;

import twitter4j.Status;

public class StatusFragment extends OpenFeedFragment {

    private static final String SAVED_STATUS = "SAVED_STATUS";

    private StatusViewHolder mViewHolder;
    private Status mStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        if (savedInstanceState != null) {
            mStatus = (Status) savedInstanceState.getSerializable(SAVED_STATUS);
        } else {
            mStatus = (Status) getActivity().getIntent().getSerializableExtra(StatusActivity.EXTRA_STATUS);
        }

        mViewHolder = new StatusViewHolder(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatus(mStatus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_STATUS, mStatus);
        super.onSaveInstanceState(outState);
    }

    private void updateStatus(Status status) {
        mStatus = status;
        mViewHolder.updateView(mStatus);
    }

    @Subscribe
    public void onStatusRetweeted(TwitterEvents.RetweetedStatusEvent event) {
        updateStatus(event.getResult());
    }

    @Subscribe
    public void onStatusDestroyed(TwitterEvents.DestroyedStatusEvent event) {
        updateStatus(event.getResult());
    }

    @Subscribe
    public void onFavoriteStatusCreated(TwitterEvents.CreatedFavoriteEvent event) {
        updateStatus(event.getResult());
    }

    @Subscribe
    public void onFavoriteStatusDestroyed(TwitterEvents.DestroyedFavoriteEvent event) {
        updateStatus(event.getResult());
    }
}
