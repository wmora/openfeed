package com.williammora.openfeed.adapters.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.williammora.openfeed.R;
import com.williammora.openfeed.listeners.OnViewHolderClickListener;
import com.williammora.openfeed.utils.StatusUtils;
import com.williammora.openfeed.utils.UserUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

public class StatusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_-]+)");
    private static final String MENTION_SCHEME = "openfeed-android://users/";

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([A-Za-z0-9_-]+)");
    private static final String HASHTAG_SCHEME = "openfeed-android://search/";

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
        if (status.isRetweet()) {
            String retweetedBy = String.format(mContext.getResources()
                    .getString(R.string.status_retweeted_by_prefix), status.getUser().getName());
            mRetweetedByText.setText(retweetedBy);
            mRetweetedByLayout.setVisibility(View.VISIBLE);
            status = status.getRetweetedStatus();
        } else {
            mRetweetedByLayout.setVisibility(View.GONE);
        }

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
                        .error(R.color.cardview_light_background)
                        .transform(new StatusImageTransformation(itemView, status.getId()))
                        .into(statusImage);
                mStatusImageHolder.addView(statusImage);
                statusText = statusText.replace(mediaEntity.getURL(), "");
            }
        }

        Picasso.with(mStatusUserPic.getContext()).cancelRequest(mStatusUserPic);
        Picasso.with(mStatusUserPic.getContext())
                .load(status.getUser().getBiggerProfileImageURLHttps())
                .error(R.drawable.ic_launcher)
                .into(mStatusUserPic);
        mStatusUserName.setText(status.getUser().getName());
        mStatusUserScreenName.setText(UserUtils.getFullScreenName(status.getUser()));
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
                // TODO: Retweet
                toggleRetweetsButton(view);
            }
        });
        if (status.isRetweetedByMe()) {
            toggleRetweetsButton(mRetweetsButton);
        }
        mFavoritesText.setTextColor(mContext.getResources().getColor(R.color.openfeed_text_secondary_color));
        mFavoritesButton.setSelected(false);
        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Favorite
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
        Linkify.addLinks(mStatusText, MENTION_PATTERN, MENTION_SCHEME, null,
                new PrefixTransformFilter("@"));
    }

    public void linkifyHashtags() {
        Linkify.addLinks(mStatusText, HASHTAG_PATTERN, HASHTAG_SCHEME, null,
                new PrefixTransformFilter("#"));
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

    private class PrefixTransformFilter implements Linkify.TransformFilter {

        String prefix;

        public PrefixTransformFilter(String prefix) {
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

    private class StatusImageTransformation implements Transformation {

        private View mHolder;
        private long id;

        public StatusImageTransformation(View holder, long statusId) {
            mHolder = holder;
            id = statusId;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            float aspectRatio = (float) source.getHeight() / (float) source.getWidth();
            int width = mHolder.getWidth();
            int height = (int) (width * aspectRatio);
            Bitmap transformedBitmap = Bitmap.createScaledBitmap(source, width, height, false);
            if (transformedBitmap != source) {
                source.recycle();
            }
            return transformedBitmap;
        }

        @Override
        public String key() {
            return String.format("%s_%d_%d", getClass().getSimpleName(), id, mHolder.getId());
        }
    }

}
