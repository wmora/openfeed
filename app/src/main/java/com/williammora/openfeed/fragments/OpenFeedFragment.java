package com.williammora.openfeed.fragments;

import android.app.Fragment;

import com.williammora.openfeed.utils.BusProvider;

public abstract class OpenFeedFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
