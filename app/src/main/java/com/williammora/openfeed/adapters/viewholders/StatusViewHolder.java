package com.williammora.openfeed.adapters.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.williammora.openfeed.R;
import com.williammora.openfeed.utils.StatusUtils;
import com.williammora.openfeed.utils.UserUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.MediaEntity;
import twitter4j.Status;

public class StatusViewHolder extends RecyclerView.ViewHolder {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_-]+)");
    private static final String MENTION_SCHEME = "http://www.twitter.com/";

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([A-Za-z0-9_-]+)");
    private static final String HASHTAG_SCHEME = "http://www.twitter.com/search/";

    private static final Pattern URL_PATTERN = Patterns.WEB_URL;

    private Context mContext;

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

    public StatusViewHolder(View v, Context context) {
        super(v);
        mContext = context;
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
        mStatusText.setText(status.getText());
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
                view.setSelected(!view.isSelected());
                int color = view.isSelected() ? R.color.openfeed_text_primary_color : R.color.openfeed_text_secondary_color;
                mRetweetsText.setTextColor(mContext.getResources().getColor(color));
                mRetweetIcon.setSelected(view.isSelected());
            }
        });
        mFavoritesText.setTextColor(mContext.getResources().getColor(R.color.openfeed_text_secondary_color));
        mFavoritesButton.setSelected(false);
        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Favorite
                view.setSelected(!view.isSelected());
                int color = view.isSelected() ? R.color.openfeed_text_primary_color : R.color.openfeed_text_secondary_color;
                mFavoritesText.setTextColor(mContext.getResources().getColor(color));
                mFavoriteIcon.setSelected(view.isSelected());
            }
        });
        linkifyStatusText();
        setTag(status);
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
