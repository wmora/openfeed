package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.williammora.openfeed.R;
import com.williammora.openfeed.events.TwitterEvents;
import com.williammora.openfeed.picasso.ProfileBannerTransformation;
import com.williammora.openfeed.services.TwitterService;
import com.williammora.openfeed.utils.UserUtils;

import twitter4j.User;

public class UserFragment extends OpenFeedFragment {

    public static final String SAVED_USER = "SAVED_USER";

    public interface UserFragmentListener {
        public User getUser();

        public String getUserScreenName();
    }

    private UserFragmentListener mListener;
    private User mUser;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUser = (User) savedInstanceState.getSerializable(SAVED_USER);
        }
        mView = inflater.inflate(R.layout.fragment_user, container, false);
        if (mUser != null) {
            updateView(mUser);
        } else {
            loadUser(mListener.getUserScreenName());
        }
        return mView;
    }

    private void loadUser(String screenName) {
        TwitterService.getInstance().showUser(screenName);
    }

    public void updateView(User user) {
        ImageView profileBanner = (ImageView) mView.findViewById(R.id.profile_banner);
        Picasso.with(profileBanner.getContext())
                .load(user.getProfileBannerMobileRetinaURL())
                .transform(new ProfileBannerTransformation(profileBanner, user.getId()))
                .placeholder(R.drawable.profile_banner)
                .into(profileBanner);
        ImageView profileImage = (ImageView) mView.findViewById(R.id.profile_image);
        Picasso.with(mView.getContext())
                .load(user.getBiggerProfileImageURLHttps())
                .into(profileImage);
        TextView profileName = (TextView) mView.findViewById(R.id.profile_name);
        profileName.setText(user.getName());
        TextView profileScreenName = (TextView) mView.findViewById(R.id.profile_screen_name);
        profileScreenName.setText(UserUtils.getFullScreenName(user));
        mUser = user;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof UserFragmentListener) {
            mListener = (UserFragmentListener) activity;
            if (mUser == null) {
                mUser = mListener.getUser();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_USER, mUser);
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void onUserEvent(TwitterEvents.UserEvent event) {
        updateView(event.getResult());
    }
}
