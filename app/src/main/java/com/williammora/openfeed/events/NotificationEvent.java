package com.williammora.openfeed.events;

public class NotificationEvent extends Event<String> {

    public NotificationEvent(String result) {
        super(result);
    }
}