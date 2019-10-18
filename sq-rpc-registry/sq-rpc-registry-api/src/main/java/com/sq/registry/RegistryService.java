package com.sq.registry;

import java.util.List;

import com.sq.common.URL;


/**
 * RegistryService. (SPI, Prototype, ThreadSafe)
 *
 * @see Registry
 * @see RegistryFactory#getRegistry(URL)
 */
public interface RegistryService {

    /**
     * Register data, such as : provider services, consumer address, route rule, override rule and other data.
     * <p>
     * Registering is required to support the contract:<br>
     * 1. When the URL sets the check=false parameter. When the registration fails, the exception is not thrown and retried in the background. Otherwise, the exception will be thrown.<br>
     * 2. When URL sets the dynamic=false parameter, it needs to be stored persistently, otherwise, it should be deleted automatically when the registrant has an abnormal exit.<br>
     * 3. When the URL sets category=routers, it means classified storage, the default category is providers, and the data can be notified by the classified section. <br>
     * 4. When the registry is restarted, network jitter, data can not be lost, including automatically deleting data from the broken line.<br>
     * 5. Allow URLs which have the same URL but different parameters to coexist,they can't cover each other.<br>
     *
     * @param url  Registration information , is not allowed to be empty, e.g:
     *             grpc://10.20.153.10/xx.BarService?version=1.0.0&application=kylin
     */
    void register(URL url);

    /**
     * Unregister
     * <p>
     * Unregistering is required to support the contract:<br>
     * 1. If it is the persistent stored data of dynamic=false, the registration data can not be found, then the IllegalStateException is thrown, otherwise it is ignored.<br>
     * 2. Unregister according to the full url match.<br>
     *
     * @param url Registration information , is not allowed to be empty, e.g:
     *            grpc://10.20.153.10/xxx.foo.BarService?version=1.0.0&application=kylin
     */
    void unregister(URL url);

    /**
     * Subscribe to eligible registered data and automatically push when the registered data is changed.
     * <p>
     * Subscribing need to support contracts:<br>
     * 1. When the URL sets the check=false parameter. When the registration fails, the exception is not thrown and retried in the background. <br>
     * 2. When URL sets category=routers, it only notifies the specified classification data. Multiple classifications are separated by commas, and allows asterisk to match, which indicates that all categorical data are subscribed.<br>
     * 3. Allow interface, group, version, and classifier as a conditional query, e.g.: interface=xx.foo.BarService&version=1.0.0<br>
     * 4. And the query conditions allow the asterisk to be matched, subscribe to all versions of all the packets of all interfaces, e.g. :interface=*&group=*&version=*&classifier=*<br>
     * 5. When the registry is restarted and network jitter, it is necessary to automatically restore the subscription request.<br>
     * 6. Allow URLs which have the same URL but different parameters to coexist,they can't cover each other.<br>
     * 7. The subscription process must be blocked, when the first notice is finished and then returned.<br>
     *
     * @param url      Subscription condition, not allowed to be empty, e.g. consumer://10.20.153.10/xx.foo.BarService?version=1.0.0&application=kylin
     * @param listener A listener of the change event, not allowed to be empty
     */
    void subscribe(URL url, NotifyListener listener);

    /**
     * Unsubscribe
     * <p>
     * Unsubscribing is required to support the contract:<br>
     * 1. If don't subscribe, ignore it directly.<br>
     * 2. Unsubscribe by full URL match.<br>
     *
     * @param url      Subscription condition, not allowed to be empty, e.g. consumer://10.20.153.10/xx.foo.BarService?version=1.0.0&application=kylin
     * @param listener A listener of the change event, not allowed to be empty
     */
    void unsubscribe(URL url, NotifyListener listener);

    /**
     * Query the registered data that matches the conditions. Corresponding to the push mode of the subscription, this is the pull mode and returns only one result.
     *
     * @param url Query condition, is not allowed to be empty, e.g. consumer://10.20.153.10/xx.foo.BarService?version=1.0.0&application=kylin
     * @return The registered information list, which may be empty, the meaning is the same as the parameters of {@link NotifyListener#notify(List<URL>)}.
     * @see NotifyListener#notify(List)
     */
    List<URL> lookup(URL url);

}