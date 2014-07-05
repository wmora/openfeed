package com.williammora.openfeed.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.williammora.openfeed.R;
import com.williammora.openfeed.activities.StatusActivity;
import com.williammora.openfeed.adapters.viewholders.StatusViewHolder;

import twitter4j.Status;

public class StatusFragment extends Fragment {

    private static final String SAVED_STATUS = "SAVED_STATUS";

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

        StatusViewHolder viewHolder = new StatusViewHolder(view, view.getContext());
        viewHolder.updateView(mStatus);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_STATUS, mStatus);
        super.onSaveInstanceState(outState);
    }
}
