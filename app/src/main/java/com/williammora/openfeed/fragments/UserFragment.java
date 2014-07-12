package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.williammora.openfeed.R;
import com.williammora.openfeed.picasso.ProfileBannerTransformation;
import com.williammora.openfeed.utils.UserUtils;

import twitter4j.User;

public class UserFragment extends Fragment {

    public static final String SAVED_USER = "SAVED_USER";

    public interface UserFragmentListener {
        public User getUser();
    }

    private UserFragmentListener mListener;
    private User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUser = (User) savedInstanceState.getSerializable(SAVED_USER);
        }
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        ImageView profileBanner = (ImageView) rootView.findViewById(R.id.profile_banner);
        Picasso.with(profileBanner.getContext())
                .load(mUser.getProfileBannerMobileRetinaURL())
                .transform(new ProfileBannerTransformation(profileBanner, mUser.getId()))
                .placeholder(R.drawable.profile_banner)
                .into(profileBanner);
        ImageView profileImage = (ImageView) rootView.findViewById(R.id.profile_image);
        Picasso.with(rootView.getContext())
                .load(mUser.getBiggerProfileImageURLHttps())
                .into(profileImage);
        TextView profileName = (TextView) rootView.findViewById(R.id.profile_name);
        profileName.setText(mUser.getName());
        TextView profileScreenname = (TextView) rootView.findViewById(R.id.profile_screenname);
        profileScreenname.setText(UserUtils.getFullScreenName(mUser));
        return rootView;
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
}
