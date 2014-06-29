package com.williammora.openfeed.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.williammora.openfeed.R;
import com.williammora.openfeed.adapters.FeedAdapter;

public class HomeFeedFragment extends Fragment {

    private RecyclerView mFeed;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mFeed = (RecyclerView) rootView.findViewById(R.id.feed);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mFeed.setLayoutManager(mLayoutManager);

        mAdapter = new FeedAdapter(new String[]{"Feed 1", "Feed 2", "Feed 3", "Feed 4", "Feed 5"});
        mFeed.setAdapter(mAdapter);

        return rootView;
    }

}
