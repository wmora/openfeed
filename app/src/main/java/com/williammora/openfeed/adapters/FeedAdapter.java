package com.williammora.openfeed.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.williammora.openfeed.R;
import com.williammora.openfeed.utils.UserUtils;

import java.util.List;

import twitter4j.Status;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<Status> mDataset;

    public FeedAdapter(List<Status> dataset) {
        mDataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_feed, parent, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Status status = mDataset.get(position);

        Picasso.with(holder.mFeedUserPic.getContext()).cancelRequest(holder.mFeedUserPic);
        Picasso.with(holder.mFeedUserPic.getContext())
                .load(status.getUser().getOriginalProfileImageURLHttps())
                .error(R.drawable.ic_launcher)
                .into(holder.mFeedUserPic);
        holder.mFeedText.setText(status.getText());
        holder.mFeedUserName.setText(status.getUser().getName());
        holder.mFeedUserScreenName.setText(UserUtils.getFullScreenName(status.getUser()));
        holder.setTag(String.valueOf(mDataset.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
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
        notifyItemRangeInserted(0, statuses.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mFeedUserPic;
        public TextView mFeedUserName;
        public TextView mFeedUserScreenName;
        public TextView mFeedText;

        public ViewHolder(View v) {
            super(v);
            mFeedUserPic = (ImageView) v.findViewById(R.id.feed_user_pic);
            mFeedUserName = (TextView) v.findViewById(R.id.feed_user_name);
            mFeedUserScreenName = (TextView) v.findViewById(R.id.feed_user_screenname);
            mFeedText = (TextView) v.findViewById(R.id.feed_text);
        }

        public void setTag(String tag) {
            itemView.setTag(tag);
        }
    }
}
