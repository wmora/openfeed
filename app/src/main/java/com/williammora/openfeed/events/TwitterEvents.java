package com.williammora.openfeed.events;

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

}
