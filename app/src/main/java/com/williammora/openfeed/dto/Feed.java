package com.williammora.openfeed.dto;

import java.io.Serializable;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;

public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;

    private Paging paging;
    private List<Status> statuses;
    private String query;

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

}
