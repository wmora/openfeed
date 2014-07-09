package com.williammora.openfeed.dto;

import java.io.Serializable;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;

public class UserFeed implements Serializable {

    private static final long serialVersionUID = 1L;

    List<Status> statuses;
    Paging paging;

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
