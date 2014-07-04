package com.williammora.openfeed.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.williammora.openfeed.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;

public class FeedViewHolder extends RecyclerView.ViewHolder {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_-]+)");
    private static final String MENTION_SCHEME = "http://www.twitter.com/";

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([A-Za-z0-9_-]+)");
    private static final String HASHTAG_SCHEME = "http://www.twitter.com/search/";

    private static final Pattern URL_PATTERN = Patterns.WEB_URL;

    public ImageView mStatusUserPic;
    public TextView mStatusUserName;
    public TextView mStatusUserScreenName;
    public TextView mStatusText;
    public TextView mStatusCreated;
    public LinearLayout mRetweetedByLayout;
    public TextView mRetweetedByText;
    public TextView mRetweetsText;
    public TextView mFavoritesText;

    public FeedViewHolder(View v) {
        super(v);
        mStatusUserPic = (ImageView) v.findViewById(R.id.status_user_pic);
        mStatusUserName = (TextView) v.findViewById(R.id.status_user_name);
        mStatusUserScreenName = (TextView) v.findViewById(R.id.status_user_screenname);
        mStatusText = (TextView) v.findViewById(R.id.status_text);
        mStatusCreated = (TextView) v.findViewById(R.id.status_created);
        mRetweetedByLayout = (LinearLayout) v.findViewById(R.id.status_retweeted_by_layout);
        mRetweetedByText = (TextView) v.findViewById(R.id.status_retweeted_by);
        mRetweetsText = (TextView) v.findViewById(R.id.status_retweets);
        mFavoritesText = (TextView) v.findViewById(R.id.status_favorites);
    }

    public void setTag(Status tag) {
        itemView.setTag(tag);
    }

    public void linkifyStatusText() {
        Linkify.addLinks(mStatusText, MENTION_PATTERN, MENTION_SCHEME, null, filter);
        Linkify.addLinks(mStatusText, HASHTAG_PATTERN, HASHTAG_SCHEME, null, filter);
        Linkify.addLinks(mStatusText, URL_PATTERN, null, null, filter);
    }

    private final Linkify.TransformFilter filter = new Linkify.TransformFilter() {
        public final String transformUrl(final Matcher match, String url) {
            return match.group();
        }
    };

}
