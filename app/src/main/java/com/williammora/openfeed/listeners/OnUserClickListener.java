package com.williammora.openfeed.listeners;

import android.content.Intent;
import android.view.View;

import com.williammora.openfeed.activities.UserActivity;

import twitter4j.User;

public class OnUserClickListener implements View.OnClickListener {

    private User mUser;

    public OnUserClickListener(User user) {
        mUser = user;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(view.getContext(), UserActivity.class);
        intent.putExtra(UserActivity.EXTRA_USER, mUser);
        view.getContext().startActivity(intent);
    }

}
