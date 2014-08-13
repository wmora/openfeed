package com.williammora.openfeed.activities;

import android.app.Activity;

import com.williammora.openfeed.utils.BusProvider;

public class OpenFeedActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
