package com.williammora.openfeed.utils;

import twitter4j.User;

public class UserUtils {

    public static String getFullScreenName(User user) {
        return user != null ? String.format("@%s", user.getScreenName()) : "";
    }

}
