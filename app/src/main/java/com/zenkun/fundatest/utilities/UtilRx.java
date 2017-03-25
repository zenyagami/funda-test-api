package com.zenkun.fundatest.utilities;

/**
 * Created by Zen zenyagami@gmail.com on 25/03/2017.
 */

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public final class UtilRx {

    private UtilRx() {
    }

    public static Subscription unsubscribe(Subscription sub) {
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
        return null;
    }
    public static void unsubscribe(Subscription... subscriptions)
    {
        for (Subscription subs: subscriptions) {
            unsubscribe(subs);
        }
    }
    public static CompositeSubscription unsubscribe(CompositeSubscription sub) {
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
        return null;
    }
}
