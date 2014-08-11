package com.williammora.openfeed.utils;

import com.williammora.openfeed.events.OpenFeedBus;

public final class BusProvider {
    private static OpenFeedBus BUS = new OpenFeedBus();

    public static OpenFeedBus getInstance() {
        return BUS;
    }

    private BusProvider() {
    }
}
