package com.sq.registry;

import java.util.List;

import com.sq.common.URL;

/**
 * NotifyListener. (API, Prototype, ThreadSafe)
 *
 * @see RegistryService#subscribe(URL, NotifyListener)
 */
public interface NotifyListener {

    /**
     * Triggered when a services change notification is received.
     * <p>
     * Notify needs to support the contract: <br>
     * 1. Always notifications on the services interface and the dimension of the data type. that is, won't notify part of the same type data belonging to one services. Users do not need to compare the results of the previous notification.<br>
     * 2. The first notification at a subscription must be a full notification of all types of data of a services.<br>
     * 3. At the time of change, different types of data are allowed to be notified separately, e.g.: providers, consumers, routers, overrides. It allows only one of these types to be notified, but the data of this type must be full, not incremental.<br>
     * 4. If a data type is empty, need to notify a empty protocol with category parameter identification of url data.<br>
     * 5. The order of notifications to be guaranteed by the notifications(That is, the implementation of the registry). Such as: single thread push, queue serialization, and version comparison.<br>
     *
     */
    void notify(List<URL> urls);

}