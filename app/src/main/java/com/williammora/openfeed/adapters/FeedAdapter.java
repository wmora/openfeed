package com.williammora.openfeed.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.williammora.openfeed.R;
import com.williammora.openfeed.utils.StatusUtils;
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
                .inflate(R.layout.list_item_status, parent, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Status status = mDataset.get(position);

        if (status.isRetweet()) {
            status = status.getRetweetedStatus();
        }

        Picasso.with(holder.mStatusUserPic.getContext()).cancelRequest(holder.mStatusUserPic);
        Picasso.with(holder.mStatusUserPic.getContext())
                .load(status.getUser().getBiggerProfileImageURLHttps())
                .error(R.drawable.ic_launcher)
                .into(holder.mStatusUserPic);
        holder.mStatusText.setText(status.getText());
        holder.mStatusUserName.setText(status.getUser().getName());
        holder.mStatusUserScreenName.setText(UserUtils.getFullScreenName(status.getUser()));
        holder.mStatusCreated.setText(StatusUtils.getCreatedText(status));
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
        public ImageView mStatusUserPic;
        public TextView mStatusUserName;
        public TextView mStatusUserScreenName;
        public TextView mStatusText;
        public TextView mStatusCreated;

        public ViewHolder(View v) {
            super(v);
            mStatusUserPic = (ImageView) v.findViewById(R.id.status_user_pic);
            mStatusUserName = (TextView) v.findViewById(R.id.status_user_name);
            mStatusUserScreenName = (TextView) v.findViewById(R.id.status_user_screenname);
            mStatusText = (TextView) v.findViewById(R.id.status_text);
            mStatusCreated = (TextView) v.findViewById(R.id.status_created);
        }

        public void setTag(String tag) {
            itemView.setTag(tag);
        }
    }
}
