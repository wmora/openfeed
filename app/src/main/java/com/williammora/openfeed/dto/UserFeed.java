package com.williammora.openfeed.dto;

import java.io.Serializable;
import java.util.List;

import twitter4j.Status;

public class UserFeed implements Serializable {

    List<Status> statuses;

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }
}
