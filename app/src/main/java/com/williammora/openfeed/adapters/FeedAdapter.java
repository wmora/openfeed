package com.williammora.openfeed.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.williammora.openfeed.R;

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
        holder.setText(mDataset.get(position).getText());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFeedText;

        public ViewHolder(View v) {
            super(v);
            mFeedText = (TextView) v.findViewById(R.id.feed_text);
        }

        private void setText(String text) {
            mFeedText.setText(text);
        }

        public void setTag(String tag) {
            itemView.setTag(tag);
        }
    }
}
