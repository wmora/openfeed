package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.williammora.openfeed.R;
import com.williammora.openfeed.picasso.ProfileBannerTransformation;
import com.williammora.openfeed.services.TwitterService;
import com.williammora.openfeed.utils.UserUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class UserFragment extends Fragment {

    public static final String SAVED_USER = "SAVED_USER";

    public interface UserFragmentListener {
        public User getUser();

        public String getUserScreenName();

        public void onUserLoaded(User user);
    }

    private UserFragmentListener mListener;
    private User mUser;
    private View mView;
    private Twitter mTwitter;

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
        new GetUserTask().execute(screenName);
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
        mListener.onUserLoaded(user);
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
            setUpTwitterService(activity);
        }
    }

    private void setUpTwitterService(Context context) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(getString(R.string.twitter_oauth_key));
        builder.setOAuthConsumerSecret(getString(R.string.twitter_oauth_secret));
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        mTwitter = factory.getInstance();
        mTwitter.setOAuthAccessToken(TwitterService.getInstance().getAccessToken(context));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_USER, mUser);
        super.onSaveInstanceState(outState);
    }

    private class GetUserTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... strings) {
            String screenName = strings[0];
            try {
                return mTwitter.users().showUser(screenName);
            } catch (TwitterException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            updateView(user);
        }
    }
}
