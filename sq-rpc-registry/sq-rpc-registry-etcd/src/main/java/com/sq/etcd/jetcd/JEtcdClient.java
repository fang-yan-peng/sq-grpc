package com.sq.etcd.jetcd;

import static com.sq.etcd.jetcd.JEtcdClientWrapper.UTF_8;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.sq.common.URL;
import com.sq.common.utils.ExecutorUtil;
import com.sq.common.utils.NamedThreadFactory;
import com.sq.etcd.ChildListener;
import com.sq.etcd.StateListener;
import com.sq.etcd.option.Constants;
import com.sq.etcd.option.OptionUtil;
import com.sq.etcd.support.AbstractEtcdClient;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.api.Event;
import io.etcd.jetcd.api.KeyValue;
import io.etcd.jetcd.api.WatchCancelRequest;
import io.etcd.jetcd.api.WatchCreateRequest;
import io.etcd.jetcd.api.WatchGrpc;
import io.etcd.jetcd.api.WatchRequest;
import io.etcd.jetcd.api.WatchResponse;
import io.etcd.jetcd.common.exception.ClosedClientException;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.netty.util.internal.ConcurrentSet;

/**
 * etct3 client.
 */
public class JEtcdClient extends AbstractEtcdClient<JEtcdClient.EtcdWatcher> {

    private JEtcdClientWrapper clientWrapper;
    private ScheduledExecutorService reconnectSchedule;

    private ExecutorService notifyExecutor;

    private int delayPeriod;
    private Logger logger = LoggerFactory.getLogger(JEtcdClient.class);

    public JEtcdClient(URL url) {
        super(url);
        try {
            clientWrapper = new JEtcdClientWrapper(url);
            clientWrapper.setConnectionStateListener((client, state) -> {
                if (state == StateListener.CONNECTED) {
                    JEtcdClient.this.stateChanged(StateListener.CONNECTED);
                } else if (state == StateListener.DISCONNECTED) {
                    JEtcdClient.this.stateChanged(StateListener.DISCONNECTED);
                }
            });
            delayPeriod = getUrl().getParameter(Constants.REGISTRY_RETRY_PERIOD_KEY, Constants.DEFAULT_REGISTRY_RETRY_PERIOD);
            reconnectSchedule = Executors.newScheduledThreadPool(1,
                    new NamedThreadFactory("etcd3-watch-auto-reconnect"));

            notifyExecutor = new ThreadPoolExecutor(
                    1
                    , url.getParameter(Constants.ETCD3_NOTIFY_MAXTHREADS_KEYS, Constants.DEFAULT_ETCD3_NOTIFY_THREADS)
                    , Constants.DEFAULT_SESSION_TIMEOUT
                    , TimeUnit.MILLISECONDS
                    , new LinkedBlockingQueue<Runnable>(url.getParameter(Constants.DEFAULT_ETCD3_NOTIFY_QUEUES_KEY, Constants.DEFAULT_GRPC_QUEUES * 3))
                    , new NamedThreadFactory("etcd3-notify", true));

            clientWrapper.start();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void doCreatePersistent(String path) {
        clientWrapper.createPersistent(path);
    }

    @Override
    public long doCreateEphemeral(String path) {
        return clientWrapper.createEphemeral(path);
    }

    @Override
    public boolean checkExists(String path) {
        return clientWrapper.checkExists(path);
    }

    @Override
    public EtcdWatcher createChildWatcherListener(String path, ChildListener listener) {
        return new EtcdWatcher(listener);
    }

    @Override
    public List<String> addChildWatcherListener(String path, EtcdWatcher etcdWatcher) {
        return etcdWatcher.forPath(path);
    }

    @Override
    public void removeChildWatcherListener(String path, EtcdWatcher etcdWatcher) {
        etcdWatcher.unwatch();
    }

    @Override
    public List<String> getChildren(String path) {
        return clientWrapper.getChildren(path);
    }

    @Override
    public boolean isConnected() {
        return clientWrapper.isConnected();
    }

    @Override
    public long createLease(long second) {
        return clientWrapper.createLease(second);
    }

    @Override
    public long createLease(long ttl, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return clientWrapper.createLease(ttl, timeout, unit);
    }

    @Override
    public void delete(String path) {
        clientWrapper.delete(path);
    }

    @Override
    public void revokeLease(long lease) {
        clientWrapper.revokeLease(lease);
    }

    @Override
    public void doClose() {
        try {
            if (notifyExecutor != null) {
                ExecutorUtil.shutdownNow(notifyExecutor, 100);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            if (reconnectSchedule != null) {
                ExecutorUtil.shutdownNow(reconnectSchedule, 100);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            clientWrapper.doClose();
        }
    }

    @Override
    public String getKVValue(String key) {
        return clientWrapper.getKVValue(key);
    }

    @Override
    public boolean put(String key, String value) {
        return clientWrapper.put(key, value);
    }

    public ManagedChannel getChannel() {
        return clientWrapper.getChannel();
    }

    public class EtcdWatcher implements StreamObserver<WatchResponse> {

        protected WatchGrpc.WatchStub watchStub;
        protected StreamObserver<WatchRequest> watchRequest;
        protected long watchId;
        protected String path;
        protected Throwable throwable;
        protected volatile Set<String> urls = new ConcurrentSet<>();
        private ChildListener listener;

        protected ReentrantLock lock = new ReentrantLock(true);

        public EtcdWatcher(ChildListener listener) {
            this.listener = listener;
        }

        @Override
        public void onNext(WatchResponse response) {

            // prevents grpc on sending watchResponse to a closed watch client.
            if (!isConnected()) {
                return;
            }

            watchId = response.getWatchId();

            if (listener != null) {
                int modified = 0;
                String service = null;
                Iterator<Event> iterator = response.getEventsList().iterator();
                while (iterator.hasNext()) {
                    Event event = iterator.next();
                    switch (event.getType()) {
                        case PUT: {
                            if (((service = find(event)) != null)
                                    && safeUpdate(service, true)) {
                                modified++;
                            }
                            break;
                        }
                        case DELETE: {
                            if (((service = find(event)) != null)
                                    && safeUpdate(service, false)) {
                                modified++;
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
                if (modified > 0) {
                    notifyExecutor.execute(() -> listener.childChanged(path, new ArrayList<>(urls)));
                }

            }
        }

        @Override
        public void onError(Throwable e) {
            tryReconnect(e);
        }

        public void unwatch() {

            // prevents grpc on sending watchResponse to a closed watch client.
            if (!isConnected()) {
                return;
            }

            try {
                this.listener = null;
                if (watchRequest != null) {
                    WatchCancelRequest watchCancelRequest =
                            WatchCancelRequest.newBuilder().setWatchId(watchId).build();
                    WatchRequest cancelRequest = WatchRequest.newBuilder()
                            .setCancelRequest(watchCancelRequest).build();
                    this.watchRequest.onNext(cancelRequest);
                }
            } catch (Exception ignored) {
                logger.warn("Failed to cancel watch for path '" + path + "'", ignored);
            }
        }

        public List<String> forPath(String path) {

            if (!isConnected()) {
                throw new ClosedClientException("watch client has been closed, path '" + path + "'");
            }
            if (this.path != null) {
                unwatch();
            }

            this.path = path;

            lock.lock();
            try {

                this.watchStub = WatchGrpc.newStub(clientWrapper.getChannel());
                this.watchRequest = watchStub.watch(this);
                this.watchRequest.onNext(nextRequest());

                List<String> children = clientWrapper.getChildren(path);
                /**
                 * caching the current services
                 */
                if (!children.isEmpty()) {
                    this.urls.addAll(filterChildren(children));
                }

                return new ArrayList<>(urls);
            } finally {
                lock.unlock();
            }
        }

        private boolean safeUpdate(String service, boolean add) {
            lock.lock();
            try {
                /**
                 * If the collection already contains the specified services, do nothing
                 */
                return add ? this.urls.add(service) : this.urls.remove(service);
            } finally {
                lock.unlock();
            }
        }

        private String find(Event event) {
            KeyValue keyValue = event.getKv();
            String key = keyValue.getKey().toStringUtf8();

            int len = path.length(), index = len, count = 0;
            if (key.length() >= index) {
                for (; (index = key.indexOf(Constants.PATH_SEPARATOR, index)) != -1; ++index) {
                    if (count++ > 1) {
                        break;
                    }
                }
            }

            /**
             * if children changed , we should refresh invokers
             */
            if (count == 1) {
                /**
                 * remove prefix
                 */
                return key.substring(len + 1);
            }

            return null;
        }

        private List<String> filterChildren(List<String> children) {
            if (children == null) {
                return Collections.emptyList();
            }
            if (children.size() <= 0) {
                return children;
            }
            final int len = path.length();
            return children.stream().parallel()
                    .filter(child -> {
                        int index = len, count = 0;
                        if (child.length() > len) {
                            for (; (index = child.indexOf(Constants.PATH_SEPARATOR, index)) != -1; ++index) {
                                if (count++ > 1) {
                                    break;
                                }
                            }
                        }
                        return count == 1;
                    })
                    .map(child -> child.substring(len + 1))
                    .collect(toList());
        }

        /**
         * create new watching request for current path.
         */
        protected WatchRequest nextRequest() {

            WatchCreateRequest.Builder builder = WatchCreateRequest.newBuilder()
                    .setKey(ByteString.copyFromUtf8(path))
                    .setRangeEnd(ByteString.copyFrom(
                            OptionUtil.prefixEndOf(ByteSequence.from(path, UTF_8)).getBytes()))
                    .setProgressNotify(true);

            return WatchRequest.newBuilder().setCreateRequest(builder).build();
        }

        public void tryReconnect(Throwable e) {

            this.throwable = e;

            logger.error("watcher client has error occurred, current path '" + path + "'", e);

            // prevents grpc on sending error to a closed watch client.
            if (!isConnected()) {
                return;
            }


            Status status = Status.fromThrowable(e);
            // system may be recover later, current connect won't be lost
            if (OptionUtil.isHaltError(status) || OptionUtil.isNoLeaderError(status)) {
                reconnectSchedule.schedule(this::reconnect, new Random().nextInt(delayPeriod), TimeUnit.MILLISECONDS);
                return;
            }
            // reconnect with a delay; avoiding immediate retry on a long connection downtime.
            reconnectSchedule.schedule(this::reconnect, new Random().nextInt(delayPeriod), TimeUnit.MILLISECONDS);
        }

        protected synchronized void reconnect() {
            this.closeWatchRequest();
            this.recreateWatchRequest();
        }

        protected void recreateWatchRequest() {
            if (watchRequest == null) {
                this.watchStub = WatchGrpc.newStub(clientWrapper.getChannel());
                this.watchRequest = watchStub.watch(this);
            }
            this.watchRequest.onNext(nextRequest());
            this.throwable = null;
            logger.warn("watch client retried connect for path '" + path + "', connection status : " + isConnected());
        }

        protected void closeWatchRequest() {
            if (this.watchRequest == null) {
                return;
            }
            this.watchRequest.onCompleted();
            this.watchRequest = null;
        }

        @Override
        public void onCompleted() {
            // do not touch this method, if you want terminate this stream.
        }
    }
}
