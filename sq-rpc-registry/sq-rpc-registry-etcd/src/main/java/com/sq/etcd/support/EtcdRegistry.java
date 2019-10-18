package com.sq.etcd.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.ConcurrentHashSet;
import com.sq.common.utils.StringUtils;
import com.sq.common.utils.UrlUtils;
import com.sq.etcd.ChildListener;
import com.sq.etcd.EtcdClient;
import com.sq.etcd.EtcdTransporter;
import com.sq.etcd.StateListener;
import com.sq.etcd.option.Constants;
import com.sq.etcd.option.OptionUtil;
import com.sq.registry.NotifyListener;
import com.sq.registry.support.FailbackRegistry;


/**
 * Support for ectd3 registry.
 */
public class EtcdRegistry extends FailbackRegistry {

    private final static Logger logger = LoggerFactory.getLogger(EtcdRegistry.class);

    private final static int DEFAULT_ETCD_PORT = 2379;

    private final static String DEFAULT_ROOT = "grpc";

    private final String root;

    private final Set<String> anyServices = new ConcurrentHashSet<>();

    private final ConcurrentMap<URL, ConcurrentMap<NotifyListener, ChildListener>> etcdListeners = new ConcurrentHashMap<>();
    private final EtcdClient etcdClient;

    public EtcdRegistry(URL url, EtcdTransporter etcdTransporter) {
        super(url);
        if (url.isAnyHost()) {
            throw new IllegalStateException("registry address is invalid, actual: '" + url.getHost() + "'");
        }
        String group = url.getParameter(Constants.GROUP_KEY, DEFAULT_ROOT);
        if (!group.startsWith(Constants.PATH_SEPARATOR)) {
            group = Constants.PATH_SEPARATOR + group;
        }
        this.root = group;
        etcdClient = etcdTransporter.connect(url);

        etcdClient.addStateListener(state -> {
            if (state == StateListener.CONNECTED) {
                try {
                    recover();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    protected static String appendDefaultPort(String address) {
        if (address != null && address.length() > 0) {
            int i = address.indexOf(':');
            if (i < 0) {
                return address + ":" + DEFAULT_ETCD_PORT;
            } else if (Integer.parseInt(address.substring(i + 1)) == 0) {
                return address.substring(0, i + 1) + DEFAULT_ETCD_PORT;
            }
        }
        return address;
    }

    @Override
    public void doRegister(URL url) {
        try {
            String path = toUrlPath(url);
            if (url.getParameter(Constants.DYNAMIC_KEY, true)) {
                etcdClient.createEphemeral(path);
                return;
            }
            etcdClient.create(path);
        } catch (Throwable e) {
            throw new RpcException("Failed to register " + url + " to etcd " + getUrl()
                    + ", cause: " + (OptionUtil.isProtocolError(e)
                    ? "etcd3 registry may not be supported yet or etcd3 registry is not available."
                    : e.getMessage()), e);
        }
    }

    @Override
    public void doUnregister(URL url) {
        try {
            String path = toUrlPath(url);
            etcdClient.delete(path);
        } catch (Throwable e) {
            throw new RpcException("Failed to unregister " + url + " to etcd " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void doSubscribe(URL url, NotifyListener listener) {
        try {
            if (Constants.ANY_VALUE.equals(url.getServiceInterface())) {
                String root = toRootPath();

                /*
                 *  if we are interested in all interfaces,
                 *  find out the current container or create one for the url, put or get only once.
                 */
                ConcurrentMap<NotifyListener, ChildListener> listeners =
                        Optional.ofNullable(etcdListeners.get(url))
                                .orElseGet(() -> {
                                    ConcurrentMap<NotifyListener, ChildListener> container, prev;
                                    prev = etcdListeners.putIfAbsent(url, container = new ConcurrentHashMap<>());
                                    return prev != null ? prev : container;
                                });

                /*
                 *  if we have no interface watcher listener,
                 *  find the current listener or create one for the current root, put or get only once.
                 */
                ChildListener interfaceListener =
                        Optional.ofNullable(listeners.get(listener))
                                .orElseGet(() -> {
                                    ChildListener childListener, prev;
                                    prev = listeners.putIfAbsent(listener, childListener = (parentPath, currentChildren) -> {
                                        /*
                                         *  because etcd3 does not support direct children watch events,
                                         *  we should filter not interface events. if we watch /grpc
                                         *  and /grpc/interface, when we put a key-value pair
                                         *  {/grpc/interface/hello hello},
                                         *  we will got events in watching path /grpc.
                                         */
                                        for (String child : currentChildren) {
                                            child = URL.decode(child);
                                            if (!anyServices.contains(child)) {
                                                anyServices.add(child);
                                                /*
                                                 *  if new interface event arrived, we watch their direct children,
                                                 *  eg: /grpc/interface, /grpc/interface and so on.
                                                 */
                                                subscribe(url.setPath(child).addParameters(Constants.INTERFACE_KEY, child,
                                                        Constants.CHECK_KEY, String.valueOf(false)), listener);
                                            }
                                        }
                                    });
                                    return prev != null ? prev : childListener;
                                });

                etcdClient.create(root);
                /*
                 * at the first time, we want to pull already interface and then watch their direct children,
                 *  eg: /grpc/interface, /grpc/interface and so on.
                 */
                List<String> services = etcdClient.addChildListener(root, interfaceListener);
                for (String service : services) {
                    service = URL.decode(service);
                    anyServices.add(service);
                    subscribe(url.setPath(service).addParameters(Constants.INTERFACE_KEY, service,
                            Constants.CHECK_KEY, String.valueOf(false)), listener);
                }
            } else {
                List<URL> urls = new ArrayList<>();
                for (String path : toCategoriesPath(url)) {

                    /*
                     *  if we are interested in special categories (providers, consumers, routers and so on),
                     *  we find out the current container or create one for the url, put or get only once.
                     */
                    ConcurrentMap<NotifyListener, ChildListener> listeners =
                            Optional.ofNullable(etcdListeners.get(url))
                                    .orElseGet(() -> {
                                        ConcurrentMap<NotifyListener, ChildListener> container, prev;
                                        prev = etcdListeners.putIfAbsent(url,
                                                container = new ConcurrentHashMap<>());
                                        return prev != null ? prev : container;
                                    });

                    /*
                     *  if we have no category watcher listener,
                     *  we find out the current listener or create one for the current category, put or get only once.
                     */
                    ChildListener childListener =
                            Optional.ofNullable(listeners.get(listener))
                                    .orElseGet(() -> {
                                        ChildListener watchListener, prev;
                                        prev = listeners.putIfAbsent(listener, watchListener = (parentPath, currentChildren) -> EtcdRegistry.this.notify(url, listener,
                                                toUrlsWithEmpty(url, parentPath, currentChildren)));
                                        return prev != null ? prev : watchListener;
                                    });

                    etcdClient.create(path);
                    /*
                     * at the first time, we want to pull already category and then watch their direct children,
                     *  eg: /grpc/interface/providers, /grpc/interface/consumers and so on.
                     */
                    List<String> children = etcdClient.addChildListener(path, childListener);
                    if (children != null) {
                        urls.addAll(toUrlsWithEmpty(url, path, children));
                    }
                }
                notify(url, listener, urls);
            }
        } catch (Throwable e) {
            throw new RpcException("Failed to subscribe " + url + " to etcd " + getUrl()
                    + ", cause: " + (OptionUtil.isProtocolError(e)
                    ? "etcd3 registry may not be supported yet or etcd3 registry is not available."
                    : e.getMessage()), e);
        }
    }

    @Override
    public void doUnsubscribe(URL url, NotifyListener listener) {
        ConcurrentMap<NotifyListener, ChildListener> listeners = etcdListeners.get(url);
        if (listeners != null) {
            ChildListener etcdListener = listeners.get(listener);
            if (etcdListener != null) {
                // maybe url has many subscribed paths
                for (String path : toUnsubscribedPath(url)) {
                    etcdClient.removeChildListener(path, etcdListener);
                }
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return etcdClient.isConnected();
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            etcdClient.close();
        } catch (Exception e) {
            logger.warn("Failed to close etcd client " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    protected String toRootDir() {
        if (root.startsWith(Constants.PATH_SEPARATOR)) {
            return root;
        }
        return Constants.PATH_SEPARATOR + root;
    }

    protected String toRootPath() {
        return root;
    }

    protected String toServicePath(URL url) {
        String name = url.getServiceInterface();
        name = StringUtils.removeGrpcEnd(name);
        if (Constants.ANY_VALUE.equals(name)) {
            return toRootPath();
        }
        return toRootDir() + Constants.PATH_SEPARATOR + URL.encode(name);
    }

    protected String[] toCategoriesPath(URL url) {
        String[] categories;
        if (Constants.ANY_VALUE.equals(url.getParameter(Constants.CATEGORY_KEY))) {
            categories = new String[]{Constants.PROVIDERS_CATEGORY, Constants.CONSUMERS_CATEGORY,
                    Constants.ROUTERS_CATEGORY, Constants.CONFIGURATORS_CATEGORY};
        } else {
            categories = url.getParameter(Constants.CATEGORY_KEY, new String[]{Constants.DEFAULT_CATEGORY});
        }
        String[] paths = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            paths[i] = toServicePath(url) + Constants.PATH_SEPARATOR + categories[i];
        }
        return paths;
    }

    protected String toCategoryPath(URL url) {
        return toServicePath(url) + Constants.PATH_SEPARATOR + url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
    }

    protected String toUrlPath(URL url) {
        return toCategoryPath(url) + Constants.PATH_SEPARATOR + URL.encode(url.toFullString());
    }

    protected List<String> toUnsubscribedPath(URL url) {
        List<String> categories = new ArrayList<>();
        if (Constants.ANY_VALUE.equals(url.getServiceInterface())) {
            String group = url.getParameter(Constants.GROUP_KEY, DEFAULT_ROOT);
            if (!group.startsWith(Constants.PATH_SEPARATOR)) {
                group = Constants.PATH_SEPARATOR + group;
            }
            categories.add(group);
            return categories;
        } else {
            categories.addAll(Arrays.asList(toCategoriesPath(url)));
        }
        return categories;
    }

    protected List<URL> toUrlsWithoutEmpty(URL consumer, List<String> providers) {
        List<URL> urls = new ArrayList<>();
        if (providers != null && providers.size() > 0) {
            for (String provider : providers) {
                provider = URL.decode(provider);
                if (provider.contains(Constants.HTTP_SUBFIX_KEY)) {
                    URL url = URL.valueOf(provider);
                    if (UrlUtils.isMatch(consumer, url)) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    protected List<URL> toUrlsWithEmpty(URL consumer, String path, List<String> providers) {
        List<URL> urls = toUrlsWithoutEmpty(consumer, providers);
        if (urls == null || urls.isEmpty()) {
            int i = path.lastIndexOf('/');
            String category = i < 0 ? path : path.substring(i + 1);
            URL empty = consumer.setProtocol(Constants.EMPTY_PROTOCOL).addParameter(Constants.CATEGORY_KEY, category);
            urls.add(empty);
        }
        return urls;
    }
}
