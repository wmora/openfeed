package com.williammora.openfeed.listeners;

public interface FeedFragmentListener {

    public void onRefreshRequested();

    public void onRefreshCompleted();

    public void showGoToTopOption(boolean shouldShow);
    
}
