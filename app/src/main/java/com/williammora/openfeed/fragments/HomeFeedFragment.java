package com.williammora.openfeed.fragments;

import com.squareup.otto.Subscribe;
import com.williammora.openfeed.dto.Feed;
import com.williammora.openfeed.events.TwitterEvents;
import com.williammora.openfeed.services.TwitterService;

import twitter4j.Paging;

public class HomeFeedFragment extends AbstractFeedFragment {

    public static String TAG = HomeFeedFragment.class.getSimpleName();

    @Override
    protected void doRequest(Paging paging) {
        TwitterService.getInstance().getHomeTimeline(paging);
    }

    @Subscribe
    public void onHomeTimelineEvent(TwitterEvents.HomeTimelineEvent event) {
        Feed feed = new Feed();
        feed.setPaging(mPaging);
        feed.setStatuses(event.getResult());
        updateFeed(feed);
        onRequestCompleted();
    }
}
