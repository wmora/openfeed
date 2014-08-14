package com.williammora.openfeed.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.williammora.openfeed.R;
import com.williammora.openfeed.adapters.viewholders.StatusViewHolder;
import com.williammora.openfeed.listeners.OnViewHolderClickListener;

import java.util.List;

import twitter4j.Status;

public class FeedAdapter extends RecyclerView.Adapter<StatusViewHolder> implements View.OnClickListener {

    private List<Status> mDataset;

    private OnViewHolderClickListener<Status> itemClickListener;

    public FeedAdapter(List<Status> dataset, OnViewHolderClickListener<Status> vhClickListener) {
        mDataset = dataset;
        itemClickListener = vhClickListener;
    }

    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_status, parent, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        v.setOnClickListener(this);
        return new StatusViewHolder(v, itemClickListener);
    }

    @Override
    public void onBindViewHolder(final StatusViewHolder holder, int position) {
        Status status = mDataset.get(position);
        holder.updateView(status);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
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

    public void replace(int i, Status status) {
        mDataset.set(i, status);
        notifyItemChanged(i);
    }
}
