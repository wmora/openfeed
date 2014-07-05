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
        return new StatusViewHolder(v, mContext);
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
