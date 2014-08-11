package com.williammora.openfeed.events;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class OpenFeedBus extends Bus {

    private final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    /**
     * Posts an event to all registered handlers.
     *
     * @param event                event to post.
     * @param dispatchOnMainThread if true, ensures that events posted from different threads are
     *                             dispatched on the main thread. Note that if false, the events
     *                             may also be posted on the main thread
     */
    public void post(final Object event, boolean dispatchOnMainThread) {
        if (dispatchOnMainThread) {
            MAIN_THREAD.post(new Runnable() {
                @Override
                public void run() {
                    OpenFeedBus.super.post(event);
                }
            });
        } else {
            post(event);
        }
    }

}
