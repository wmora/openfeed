package com.williammora.openfeed.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.williammora.openfeed.BuildConfig;
import com.williammora.openfeed.utils.Preferences;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterService {

    private static TwitterService ourInstance = new TwitterService();
    private RequestToken requestToken;

    public static TwitterService getInstance() {
        return ourInstance;
    }

    private TwitterService() {
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Preferences.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isSignedIn(Context context) {
        String accessTokenKey = getSharedPreferences(context).getString(Preferences.TWITTER_ACCESS_TOKEN_KEY, "");
        return !TextUtils.isEmpty(accessTokenKey);
    }

    public void saveAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
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

    public void signOut(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(Preferences.TWITTER_ACCESS_TOKEN_KEY);
        editor.remove(Preferences.TWITTER_ACCESS_TOKEN_SECRET);
        editor.commit();
    }

    public AccessToken getAccessToken(Context context) {
        String tokenKey = getSharedPreferences(context).getString(Preferences.TWITTER_ACCESS_TOKEN_KEY, "");
        String tokenSecret = getSharedPreferences(context).getString(Preferences.TWITTER_ACCESS_TOKEN_SECRET, "");
        return new AccessToken(tokenKey, tokenSecret);
    }

    public String getTwitterOauthKey() {
        return BuildConfig.TWITTER_OAUTH_KEY;
    }

    public String getTwitterOauthSecret() {
        return BuildConfig.TWITTER_OAUTH_SECRET;
    }
}
