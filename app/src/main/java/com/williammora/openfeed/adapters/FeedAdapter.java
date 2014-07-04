package com.williammora.openfeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.williammora.openfeed.R;
import com.williammora.openfeed.adapters.viewholders.FeedViewHolder;
import com.williammora.openfeed.listeners.OnRecyclerViewItemClickListener;
import com.williammora.openfeed.utils.StatusUtils;
import com.williammora.openfeed.utils.UserUtils;

import java.util.List;

import twitter4j.Status;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> implements View.OnClickListener {

    private List<Status> mDataset;
    private Context mContext;

    private OnRecyclerViewItemClickListener<Status> itemClickListener;

    public FeedAdapter(List<Status> dataset, Context context) {
        mDataset = dataset;
        mContext = context;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_status, parent, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        v.setOnClickListener(this);
        return new FeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
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
        holder.mStatusText.setText(status.getText());
        holder.linkifyStatusText();
        holder.mStatusUserName.setText(status.getUser().getName());
        holder.mStatusUserScreenName.setText(UserUtils.getFullScreenName(status.getUser()));
        holder.mStatusCreated.setText(StatusUtils.getCreatedText(status));
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
        notifyItemRangeInserted(0, statuses.size());
    }
}
