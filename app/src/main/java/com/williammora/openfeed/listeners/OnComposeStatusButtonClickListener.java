package com.williammora.openfeed.listeners;

import android.content.Intent;
import android.view.View;

import com.williammora.openfeed.activities.ComposeStatusActivity;

import twitter4j.Status;

public class OnComposeStatusButtonClickListener implements View.OnClickListener {

    /**
     * Status that will be replied to
     */
    private Status mStatus;

    public OnComposeStatusButtonClickListener() {
        this(null);
    }

    public OnComposeStatusButtonClickListener(Status status) {
        mStatus = status;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(view.getContext(), ComposeStatusActivity.class);
        if (mStatus != null) {
            // TODO: Include status that is being replied to here
        }
        view.getContext().startActivity(intent);
    }
}
