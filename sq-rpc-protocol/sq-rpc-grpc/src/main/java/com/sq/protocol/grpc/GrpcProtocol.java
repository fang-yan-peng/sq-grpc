package com.sq.protocol.grpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.common.extension.ExtensionLoader;
import com.sq.common.threadpool.ThreadPool;
import com.sq.common.utils.CollectionUtils;
import com.sq.common.utils.ConfigUtils;
import com.sq.common.utils.StringUtils;
import com.sq.protocol.grpc.Interceptor.GrpcServerInterceptor;
import com.sq.rpc.Exporter;
import com.sq.rpc.Invoker;
import com.sq.rpc.Protocol;
import com.sq.rpc.RegistryAction;
import com.sq.rpc.protocol.AbstractProtocol;

import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * grpc协议
 * @author yanpengfang
 * create 2019-10-14 3:43 PM
 */
public class GrpcProtocol extends AbstractProtocol {

    public static final String NAME = "grpc";

    public static final int DEFAULT_PORT = 20880;

    /**
     * <host:port,Server>
     */
    private final Map<String, Server> serverMap = new ConcurrentHashMap<>();

    /**
     * <host:port,Exchanger>
     */
    private final Map<String, List<ReferenceCountMnagedChannel>> referenceClientMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Object> locks = new ConcurrentHashMap<>();

    private final List<RegistryAction> registryActions = new CopyOnWriteArrayList<>();

    public static GrpcProtocol getGrpcProtocol() {
        return (GrpcProtocol) ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(NAME);
    }

    @Override
    public void addRegistryAction(RegistryAction action) {
        registryActions.add(action);
    }

    protected static String serviceKey(URL url) {
        int port = url.getParameter(Constants.BIND_PORT_KEY, url.getPort());
        return serviceKey(port, url.getPath(), url.getParameter(Constants.VERSION_KEY),
                url.getParameter(Constants.GROUP_KEY));
    }

    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        if (invoker.getService() == null) {
            throw new RpcException("grpc export services cannot be null");
        }
        if (!(invoker.getService() instanceof BindableService)) {
            throw new RpcException("grpc export services must implement io.grpc.BindableService");
        }
        URL url = invoker.getUrl();
        String key = serviceKey(url);
        GrpcExporter<T> exporter = new GrpcExporter<>(invoker, key, exporterMap);
        exporterMap.put(key, exporter);
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        Invoker<T> invoker = new GrpcInvoker<>(type, url, getClients(url));
        invokers.add(invoker);
        return invoker;
    }

    @Override
    public void destroy() {

        for (String key : new ArrayList<>(serverMap.keySet())) {
            Server server = serverMap.remove(key);

            if (server == null) {
                continue;
            }

            try {
                if (logger.isInfoEnabled()) {
                    logger.info("Close grpc server: " + server.getPort());
                }
                server.shutdown();
            } catch (Throwable t) {
                logger.warn(t.getMessage(), t);
            }
        }
        for (String key : new ArrayList<>(referenceClientMap.keySet())) {
            List<ReferenceCountMnagedChannel> clients = referenceClientMap.remove(key);

            if (CollectionUtils.isEmpty(clients)) {
                continue;
            }

            for (ReferenceCountMnagedChannel client : clients) {
                closeReferenceCountChannel(client);
            }
        }
        registryActions.clear();
        super.destroy();
    }

    @Override
    public void start() {
        List<List<Exporter<?>>> exportersByHost = new ArrayList<>(exporterMap
                .values()
                .stream()
                .collect(Collectors
                        .groupingBy(e -> e
                                .getInvoker()
                                .getUrl()
                                .getAddress())).values());for (List<Exporter<?>> exporters : exportersByHost) {
            openServer(exporters);
        }
        for (RegistryAction action : registryActions) {
            action.register();
        }
    }

    private void openServer(List<Exporter<?>> exporters) {
        // find server.
        URL url = exporters.get(0).getInvoker().getUrl();
        String key = url.getAddress();
        //client can export a services which's only for server to invoke
        boolean isServer = url.getParameter(Constants.IS_SERVER_KEY, true);
        if (isServer) {
            Server server = serverMap.get(key);
            if (server == null) {
                synchronized (this) {
                    server = serverMap.get(key);
                    if (server == null) {
                        server = createServer(exporters);
                        serverMap.put(key, server);
                        try {
                            server.start();
                        } catch (IOException e) {
                            throw new RpcException(e);
                        }
                    }
                }
            } else {
                throw new RpcException("address:" + key + " hash already opened");
            }
        }
    }

    private Server createServer(List<Exporter<?>> exporters) {
        URL url = exporters.get(0).getInvoker().getUrl();
        NettyServerBuilder builder = NettyServerBuilder.forPort(url.getPort());
        for (Exporter<?> exporter : exporters) {
            //@todo 监控、限流、连接限制
            builder.addService(ServerInterceptors.intercept(
                    (BindableService) exporter.getInvoker().getService(),
                    new GrpcServerInterceptor(url)));
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBoss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(url.getPositiveParameter(Constants.IO_THREADS_KEY, Constants.DEFAULT_IO_THREADS),
                new DefaultThreadFactory("NettyServerWorker", true));
        builder.bossEventLoopGroup(bossGroup);
        builder.workerEventLoopGroup(workerGroup);
        builder.maxConcurrentCallsPerConnection(url.getPositiveParameter(Constants.MAX_CALLS_PER_CONNECTION_KEY, Constants.MAX_CALLS_PER_CONNECTION));
        builder.maxInboundMessageSize(url.getPositiveParameter(Constants.MAX_MESSAGE_SIZE_KEY, Constants.MAX_MESSAGE_SIZE));
        builder.executor(ExtensionLoader.getExtensionLoader(ThreadPool.class).getAdaptiveExtension().getExecutor(url));
        return builder.build();
    }

    private ManagedChannel[] getClients(URL url) {
        // whether to share connection

        boolean useShareConnect = false;

        int connections = url.getParameter(Constants.CONNECTIONS_KEY, 0);
        List<ReferenceCountMnagedChannel> shareClients = null;
        // if not configured, connection is shared, otherwise, one connection for one services
        if (connections == 0) {
            useShareConnect = true;
            /**
             * The xml configuration should have a higher priority than properties.
             */
            String shareConnectionsStr = url.getParameter(Constants.SHARE_CONNECTIONS_KEY, (String) null);
            connections = Integer.parseInt(StringUtils.isBlank(shareConnectionsStr) ? ConfigUtils.getProperty(Constants.SHARE_CONNECTIONS_KEY,
                    Constants.DEFAULT_SHARE_CONNECTIONS) : shareConnectionsStr);
            shareClients = getSharedClient(url, connections);
        }

        ManagedChannel[] clients = new ManagedChannel[connections];
        for (int i = 0; i < clients.length; i++) {
            if (useShareConnect) {
                clients[i] = shareClients.get(i);
            } else {
                clients[i] = initClient(url);
            }
        }

        return clients;
    }

    /**
     * Get shared connection
     *
     * @param url
     * @param connectNum connectNum must be greater than or equal to 1
     */
    private List<ReferenceCountMnagedChannel> getSharedClient(URL url, int connectNum) {
        String key = url.getAddress();
        List<ReferenceCountMnagedChannel> clients = referenceClientMap.get(key);

        if (checkClientCanUse(clients)) {
            batchClientRefIncr(clients);
            return clients;
        }

        locks.putIfAbsent(key, new Object());
        synchronized (locks.get(key)) {
            clients = referenceClientMap.get(key);
            // check
            if (checkClientCanUse(clients)) {
                batchClientRefIncr(clients);
                return clients;
            }

            // connectNum must be greater than or equal to 1
            connectNum = Math.max(connectNum, 1);

            // If the clients is empty, then the first initialization is
            if (CollectionUtils.isEmpty(clients)) {
                clients = buildReferenceCountClientList(url, connectNum);
                referenceClientMap.put(key, clients);

            } else {
                for (int i = 0; i < clients.size(); i++) {
                    ReferenceCountMnagedChannel referenceCountExchangeClient = clients.get(i);
                    // If there is a client in the list that is no longer available, create a new one to replace him.
                    if (referenceCountExchangeClient == null || referenceCountExchangeClient.isShutdown()) {
                        clients.set(i, buildReferenceCountClient(url));
                        continue;
                    }

                    referenceCountExchangeClient.incrementAndGetCount();
                }
            }

            /**
             * I understand that the purpose of the remove operation here is to avoid the expired url key
             * always occupying this memory space.
             */
            locks.remove(key);

            return clients;
        }
    }

    /**
     * Check if the client list is all available
     *
     * @param referenceCountClients
     * @return true-available，false-unavailable
     */
    private boolean checkClientCanUse(List<ReferenceCountMnagedChannel> referenceCountClients) {
        if (CollectionUtils.isEmpty(referenceCountClients)) {
            return false;
        }

        for (ReferenceCountMnagedChannel referenceCountClient : referenceCountClients) {
            // As long as one client is not available, you need to replace the unavailable client with the available one.
            if (referenceCountClient == null || referenceCountClient.isShutdown()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Add client references in bulk
     *
     * @param referenceCountClients
     */
    private void batchClientRefIncr(List<ReferenceCountMnagedChannel> referenceCountClients) {
        if (CollectionUtils.isEmpty(referenceCountClients)) {
            return;
        }

        for (ReferenceCountMnagedChannel referenceCountExchangeClient : referenceCountClients) {
            if (referenceCountExchangeClient != null) {
                referenceCountExchangeClient.incrementAndGetCount();
            }
        }
    }

    /**
     * Bulk build client
     *
     * @param url
     * @param connectNum
     * @return
     */
    private List<ReferenceCountMnagedChannel> buildReferenceCountClientList(URL url, int connectNum) {
        List<ReferenceCountMnagedChannel> clients = new CopyOnWriteArrayList<>();

        for (int i = 0; i < connectNum; i++) {
            clients.add(buildReferenceCountClient(url));
        }

        return clients;
    }

    /**
     * Build a single client
     *
     * @param url
     * @return
     */
    private ReferenceCountMnagedChannel buildReferenceCountClient(URL url) {
        ManagedChannel exchangeClient = initClient(url);
        return new ReferenceCountMnagedChannel(exchangeClient);
    }

    /**
     * Create new connection
     *
     * @param url
     */
    private ManagedChannel initClient(URL url) {
        NettyChannelBuilder builder = NettyChannelBuilder.forAddress(url.getHost(), url.getPort())
                .maxInboundMessageSize(url.getPositiveParameter(Constants.MAX_MESSAGE_SIZE_KEY, Constants.MAX_MESSAGE_SIZE))
                .usePlaintext();
        EventLoopGroup workerGroup = new NioEventLoopGroup(url.getPositiveParameter(Constants.IO_THREADS_KEY, Constants.DEFAULT_IO_THREADS));
        return builder.eventLoopGroup(workerGroup)
                .executor(ExtensionLoader.getExtensionLoader(ThreadPool.class).getAdaptiveExtension().getExecutor(url))
                .build();
    }

    private void closeReferenceCountChannel(ReferenceCountMnagedChannel client) {
        if (client == null) {
            return;
        }
        try {
            client.shutdown();
            client.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }
}
