package com.williammora.openfeed.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.williammora.openfeed.BuildConfig;
import com.williammora.openfeed.OpenFeed;
import com.williammora.openfeed.R;
import com.williammora.openfeed.events.TwitterEvents;
import com.williammora.openfeed.utils.BusProvider;
import com.williammora.openfeed.utils.Preferences;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterService {

    private static final String TAG = TwitterService.class.getSimpleName();

    private static TwitterService ourInstance = new TwitterService();
    private RequestToken requestToken;
    private Context context;
    private AsyncTwitter twitter;

    public static TwitterService getInstance() {
        return ourInstance;
    }

    private TwitterService() {
    }

    public void init() {
        context = OpenFeed.getApplication().getApplicationContext();
        twitter = new AsyncTwitterFactory().getInstance();
        twitter.addListener(new TwitterListener());
        twitter.setOAuthConsumer(getTwitterOauthKey(), getTwitterOauthSecret());
        if (isSignedIn()) {
            twitter.setOAuthAccessToken(getAccessToken());
        }
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(Preferences.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isSignedIn() {
        String accessTokenKey = getSharedPreferences()
                .getString(Preferences.TWITTER_ACCESS_TOKEN_KEY, "");
        return !TextUtils.isEmpty(accessTokenKey);
    }

    public void saveAccessToken(AccessToken accessToken) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Preferences.TWITTER_ACCESS_TOKEN_KEY, accessToken.getToken());
        editor.putString(Preferences.TWITTER_ACCESS_TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }

    public void setRequestToken(RequestToken requestToken) {
        this.requestToken = requestToken;
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }

    public void signOut() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(Preferences.TWITTER_ACCESS_TOKEN_KEY);
        editor.remove(Preferences.TWITTER_ACCESS_TOKEN_SECRET);
        editor.commit();
    }

    public AccessToken getAccessToken() {
        String tokenKey = getSharedPreferences().getString(Preferences.TWITTER_ACCESS_TOKEN_KEY, "");
        String tokenSecret = getSharedPreferences().getString(Preferences.TWITTER_ACCESS_TOKEN_SECRET, "");
        return new AccessToken(tokenKey, tokenSecret);
    }

    public String getTwitterOauthKey() {
        return BuildConfig.TWITTER_OAUTH_KEY;
    }

    public String getTwitterOauthSecret() {
        return BuildConfig.TWITTER_OAUTH_SECRET;
    }

    public void getOAuthRequestToken() {
        Log.d(TAG, "Requesting oauth request token");
        twitter.getOAuthRequestTokenAsync(context.getString(R.string.twitter_callback_url));
    }

    public void getOAuthAccessToken(String verifier) {
        twitter.getOAuthAccessTokenAsync(TwitterService.getInstance().getRequestToken(), verifier);
    }

    public void getHomeTimeline(Paging paging) {
        twitter.getHomeTimeline(paging);
    }

    public void search(Query query) {
        twitter.search(query);
    }

    public void showUser(String screenName) {
        twitter.showUser(screenName);
    }

    private class TwitterListener extends TwitterAdapter {

        @Override
        public void gotOAuthRequestToken(RequestToken token) {
            TwitterService.getInstance().setRequestToken(token);
            BusProvider.getInstance().post(new TwitterEvents.OAuthRequestTokenEvent(token), true);
        }

        @Override
        public void gotOAuthAccessToken(AccessToken token) {
            TwitterService.getInstance().saveAccessToken(token);
            twitter.setOAuthAccessToken(getAccessToken());
            BusProvider.getInstance().post(new TwitterEvents.OAuthAccessTokenEvent(token), true);
        }

        @Override
        public void gotHomeTimeline(ResponseList<Status> statuses) {
            BusProvider.getInstance().post(new TwitterEvents.HomeTimelineEvent(statuses), true);
        }

        @Override
        public void searched(QueryResult queryResult) {
            BusProvider.getInstance().post(new TwitterEvents.SearchEvent(queryResult), true);
        }

        @Override
        public void gotUserDetail(User user) {
            BusProvider.getInstance().post(new TwitterEvents.UserEvent(user), true);
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            Log.e(getClass().getSimpleName(), method.toString(), te);
        }
    }
}
