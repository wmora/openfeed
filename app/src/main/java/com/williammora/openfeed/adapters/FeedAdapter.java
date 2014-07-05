package com.williammora.openfeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.williammora.openfeed.R;
import com.williammora.openfeed.adapters.viewholders.StatusViewHolder;
import com.williammora.openfeed.listeners.OnRecyclerViewItemClickListener;
import com.williammora.openfeed.utils.StatusUtils;
import com.williammora.openfeed.utils.UserUtils;

import java.util.List;

import twitter4j.Status;

public class FeedAdapter extends RecyclerView.Adapter<StatusViewHolder> implements View.OnClickListener {

    private List<Status> mDataset;
    private Context mContext;

    private OnRecyclerViewItemClickListener<Status> itemClickListener;

    public FeedAdapter(List<Status> dataset, Context context) {
        mDataset = dataset;
        mContext = context;
    }

    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_status, parent, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        v.setOnClickListener(this);
        return new StatusViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StatusViewHolder holder, int position) {
        Status status = mDataset.get(position);

        if (status.isRetweet()) {
            String retweetedBy = String.format(mContext.getResources()
                    .getString(R.string.status_retweeted_by_prefix), status.getUser().getName());
            holder.mRetweetedByText.setText(retweetedBy);
            holder.mRetweetedByLayout.setVisibility(View.VISIBLE);
            status = status.getRetweetedStatus();
        } else {
            holder.mRetweetedByLayout.setVisibility(View.GONE);
        }

        Picasso.with(holder.mStatusUserPic.getContext()).cancelRequest(holder.mStatusUserPic);
        Picasso.with(holder.mStatusUserPic.getContext())
                .load(status.getUser().getBiggerProfileImageURLHttps())
                .error(R.drawable.ic_launcher)
                .into(holder.mStatusUserPic);
        holder.mStatusUserName.setText(status.getUser().getName());
        holder.mStatusUserScreenName.setText(UserUtils.getFullScreenName(status.getUser()));
        holder.mStatusCreated.setText(StatusUtils.getCreatedText(status));
        holder.mStatusText.setText(status.getText());
        holder.mRetweetsText.setText(String.format("%d", status.getRetweetCount()));
        holder.mFavoritesText.setText(String.format("%d", status.getFavoriteCount()));
        holder.mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Reply
            }
        });
        holder.mRetweetsText.setTextColor(mContext.getResources().getColor(R.color.openfeed_text_secondary_color));
        holder.mRetweetsButton.setSelected(false);
        holder.mRetweetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Retweet
                view.setSelected(!view.isSelected());
                int color = view.isSelected() ? R.color.openfeed_text_primary_color : R.color.openfeed_text_secondary_color;
                holder.mRetweetsText.setTextColor(mContext.getResources().getColor(color));
                holder.mRetweetIcon.setSelected(view.isSelected());
            }
        });
        holder.mFavoritesText.setTextColor(mContext.getResources().getColor(R.color.openfeed_text_secondary_color));
        holder.mFavoritesButton.setSelected(false);
        holder.mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Favorite
                view.setSelected(!view.isSelected());
                int color = view.isSelected() ? R.color.openfeed_text_primary_color : R.color.openfeed_text_secondary_color;
                holder.mFavoritesText.setTextColor(mContext.getResources().getColor(color));
                holder.mFavoriteIcon.setSelected(view.isSelected());
            }
        });
        holder.linkifyStatusText();
        holder.setTag(status);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener<Status> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        if (itemClickListener != null) {
            Status status = (Status) view.getTag();
            itemClickListener.onItemClick(view, status);
        }
    }

    public void setDataset(List<Status> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);
        notifyItemRangeInserted(0, mDataset.size());
    }

    public void addAll(List<Status> statuses) {
        mDataset.addAll(statuses);
        notifyItemRangeInserted(mDataset.size(), statuses.size());
    }

    public void addAll(int i, List<Status> statuses) {
        mDataset.addAll(i, statuses);
        notifyItemRangeInserted(i, statuses.size());
    }
}
