package com.williammora.openfeed.utils;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

import twitter4j.Status;

public class StatusUtils {

    public static CharSequence getCreatedText(Status status) {

        Date now = Calendar.getInstance().getTime();
        Date createdDate = status.getCreatedAt();

        return DateUtils.getRelativeTimeSpanString(createdDate.getTime(), now.getTime(),
                DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
    }

}
