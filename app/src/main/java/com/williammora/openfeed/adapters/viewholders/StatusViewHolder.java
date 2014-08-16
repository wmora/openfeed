package com.williammora.openfeed.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.williammora.openfeed.R;
import com.williammora.openfeed.listeners.OnUserClickListener;
import com.williammora.openfeed.listeners.OnViewHolderClickListener;
import com.williammora.openfeed.picasso.StatusImageTransformation;
import com.williammora.openfeed.services.TwitterService;
import com.williammora.openfeed.utils.StatusUtils;
import com.williammora.openfeed.utils.UserUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

public class StatusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_-]+)");
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([A-Za-z0-9_-]+)");

    private Context mContext;
    private OnViewHolderClickListener<Status> mListener;

    public ImageView mStatusUserPic;
    public TextView mStatusUserName;
    public TextView mStatusUserScreenName;
    public TextView mStatusText;
    public LinearLayout mStatusImageHolder;
    public TextView mStatusCreated;
    public LinearLayout mRetweetedByLayout;
    public TextView mRetweetedByText;
    public TextView mRetweetsText;
    public TextView mFavoritesText;
    public LinearLayout mReplyButton;
    public LinearLayout mRetweetsButton;
    public LinearLayout mFavoritesButton;
    public ImageView mRetweetIcon;
    public ImageView mFavoriteIcon;

    public StatusViewHolder(View v) {
        this(v, null);
    }

    public StatusViewHolder(View v, OnViewHolderClickListener<Status> listener) {
        super(v);
        v.setOnClickListener(this);
        mListener = listener;
        mContext = v.getContext();
        mStatusUserPic = (ImageView) v.findViewById(R.id.status_user_pic);
        mStatusUserName = (TextView) v.findViewById(R.id.status_user_name);
        mStatusUserScreenName = (TextView) v.findViewById(R.id.status_user_screenname);
        mStatusText = (TextView) v.findViewById(R.id.status_text);
        mStatusImageHolder = (LinearLayout) v.findViewById(R.id.status_image_holder);
        mStatusCreated = (TextView) v.findViewById(R.id.status_created);
        mRetweetedByLayout = (LinearLayout) v.findViewById(R.id.status_retweeted_by_layout);
        mRetweetedByText = (TextView) v.findViewById(R.id.status_retweeted_by);
        mRetweetsText = (TextView) v.findViewById(R.id.status_retweets);
        mFavoritesText = (TextView) v.findViewById(R.id.status_favorites);
        mReplyButton = (LinearLayout) v.findViewById(R.id.status_reply_button);
        mRetweetsButton = (LinearLayout) v.findViewById(R.id.status_retweets_button);
        mFavoritesButton = (LinearLayout) v.findViewById(R.id.status_favorites_button);
        mRetweetIcon = (ImageView) v.findViewById(R.id.status_retweet_icon);
        mFavoriteIcon = (ImageView) v.findViewById(R.id.status_favorite_icon);
    }

    public void updateView(Status status) {
        setTag(status);

        final long originalStatusId = status.getId(); // To undo retweets

        if (status.isRetweet()) {
            String retweetedBy = String.format(mContext.getResources()
                    .getString(R.string.status_retweeted_by_prefix), status.getUser().getName());
            mRetweetedByText.setText(retweetedBy);
            mRetweetedByLayout.setVisibility(View.VISIBLE);
            mRetweetedByLayout.setOnClickListener(new OnUserClickListener(status.getUser()));
            status = status.getRetweetedStatus();
        } else {
            mRetweetedByLayout.setVisibility(View.GONE);
        }

        final long statusId = status.getId();

        mStatusImageHolder.removeAllViews();

        MediaEntity[] mediaEntities = status.getMediaEntities();
        String statusText = status.getText();

        for (MediaEntity mediaEntity : mediaEntities) {
            // Only photos for now
            if (mediaEntity.getType().equals("photo")) {
                final ImageView statusImage = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.status_image, null);
                Picasso.with(statusImage.getContext()).cancelRequest(statusImage);
                Picasso.with(statusImage.getContext())
                        .load(mediaEntity.getMediaURLHttps())
                        .placeholder(R.drawable.image_loader_placeholder)
                        .error(R.drawable.image_loader_placeholder)
                        .transform(new StatusImageTransformation(itemView, status.getId()))
                        .into(statusImage);
                mStatusImageHolder.addView(statusImage);
                statusText = statusText.replace(mediaEntity.getURL(), "");
            }
        }

        Picasso.with(mStatusUserPic.getContext()).cancelRequest(mStatusUserPic);
        Picasso.with(mStatusUserPic.getContext())
                .load(status.getUser().getBiggerProfileImageURLHttps())
                .placeholder(R.drawable.image_loader_placeholder)
                .error(R.drawable.image_loader_placeholder)
                .into(mStatusUserPic);
        mStatusUserPic.setOnClickListener(new OnUserClickListener(status.getUser()));
        mStatusUserName.setText(status.getUser().getName());
        mStatusUserScreenName.setText(UserUtils.getFullScreenName(status.getUser()));
        mStatusUserName.setOnClickListener(new OnUserClickListener(status.getUser()));
        mStatusUserScreenName.setOnClickListener(new OnUserClickListener(status.getUser()));
        mStatusCreated.setText(StatusUtils.getCreatedText(status));
        mStatusText.setText(statusText);
        mRetweetsText.setText(String.format("%d", status.getRetweetCount()));
        mFavoritesText.setText(String.format("%d", status.getFavoriteCount()));
        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Reply
            }
        });
        mRetweetsText.setTextColor(mContext.getResources().getColor(R.color.openfeed_text_secondary_color));
        mRetweetsButton.setSelected(false);
        mRetweetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isSelected()) {
                    TwitterService.getInstance().destroyStatus(originalStatusId);
                } else {
                    TwitterService.getInstance().retweetStatus(statusId);
                }
                toggleRetweetsButton(view);
            }
        });
        if (status.isRetweeted()) {
            toggleRetweetsButton(mRetweetsButton);
        }
        mFavoritesText.setTextColor(mContext.getResources().getColor(R.color.openfeed_text_secondary_color));
        mFavoritesButton.setSelected(false);
        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isSelected()) {
                    TwitterService.getInstance().destroyFavorite(statusId);
                } else {
                    TwitterService.getInstance().createFavorite(statusId);
                }
                toggleFavoritesButton(view);
            }
        });
        if (status.isFavorited()) {
            toggleFavoritesButton(mFavoritesButton);
        }
        for (URLEntity urlEntity : status.getURLEntities()) {
            String url = urlEntity.getURL();
            String displayUrl = urlEntity.getDisplayURL();
            mStatusText.setText(mStatusText.getText().toString().replaceFirst(url, displayUrl));
            linkifyUrl(displayUrl, urlEntity.getURL());
        }
        linkifyUserMentions();
        linkifyHashtags();
        // Set after linkifying so rest of text is handled the same as the view
        mStatusText.setOnClickListener(this);
    }

    private void toggleRetweetsButton(View view) {
        view.setSelected(!view.isSelected());
        int color = view.isSelected() ? R.color.openfeed_text_primary_color : R.color.openfeed_text_secondary_color;
        mRetweetsText.setTextColor(mContext.getResources().getColor(color));
        mRetweetIcon.setSelected(view.isSelected());
    }

    private void toggleFavoritesButton(View view) {
        view.setSelected(!view.isSelected());
        int color = view.isSelected() ? R.color.openfeed_text_primary_color : R.color.openfeed_text_secondary_color;
        mFavoritesText.setTextColor(mContext.getResources().getColor(color));
        mFavoriteIcon.setSelected(view.isSelected());
    }

    public void setTag(Status tag) {
        itemView.setTag(tag);
    }

    public void linkifyUserMentions() {
        String mentionUri = mContext.getString(R.string.twitter_mention_uri);
        Linkify.addLinks(mStatusText, MENTION_PATTERN, mentionUri, null, new TransformFilter("@"));
    }

    public void linkifyHashtags() {
        String hashtagUri = mContext.getString(R.string.twitter_hashtag_uri);
        Linkify.addLinks(mStatusText, HASHTAG_PATTERN, hashtagUri, null, new TransformFilter("#"));
    }

    private void linkifyUrl(String displayUrl, String url) {
        Linkify.addLinks(mStatusText, Pattern.compile(displayUrl), null, null,
                new UrlTransformFilter(url));
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            Status status = (Status) itemView.getTag();
            mListener.onItemClick(itemView, status);
        }
    }

    private class TransformFilter implements Linkify.TransformFilter {

        String prefix;

        public TransformFilter(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String transformUrl(Matcher matcher, String s) {
            return matcher.group().replaceFirst(prefix, "");
        }

    }

    private class UrlTransformFilter implements Linkify.TransformFilter {

        String mUrl;

        private UrlTransformFilter(String url) {
            mUrl = url;
        }

        @Override
        public String transformUrl(Matcher matcher, String s) {
            return mUrl;
        }
    }

}
