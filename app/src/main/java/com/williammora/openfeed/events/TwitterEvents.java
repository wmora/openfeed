package com.williammora.openfeed.events;

import java.util.List;

import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterEvents {

    public static class OAuthRequestTokenEvent extends Event<RequestToken> {
        public OAuthRequestTokenEvent(RequestToken result) {
            super(result);
        }
    }

    public static class OAuthAccessTokenEvent extends Event<AccessToken> {
        public OAuthAccessTokenEvent(AccessToken result) {
            super(result);
        }
    }

    public static class HomeTimelineEvent extends Event<List<Status>> {
        public HomeTimelineEvent(List<Status> result) {
            super(result);
        }
    }

    public static class SearchEvent extends Event<QueryResult> {
        public SearchEvent(QueryResult result) {
            super(result);
        }
    }

}