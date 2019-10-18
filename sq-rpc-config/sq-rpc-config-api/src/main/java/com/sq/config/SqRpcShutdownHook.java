package com.sq.config;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.extension.ExtensionLoader;
import com.sq.registry.support.AbstractRegistryFactory;
import com.sq.rpc.Protocol;


/**
 * The shutdown hook thread to do the clean up stuff.
 * This is a singleton in order to ensure there is only one shutdown hook registered.
 * Because {@link ApplicationShutdownHooks} use {@link java.util.IdentityHashMap}
 * to store the shutdown hooks.
 */
public class SqRpcShutdownHook extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SqRpcShutdownHook.class);

    private static final SqRpcShutdownHook RPC_SHUTDOWN_HOOK = new SqRpcShutdownHook("SqRpcShutdownHook");
    /**
     * Has it already been registered or not?
     */
    private final AtomicBoolean registered = new AtomicBoolean(false);
    /**
     * Has it already been destroyed or not?
     */
    private final AtomicBoolean destroyed= new AtomicBoolean(false);

    private SqRpcShutdownHook(String name) {
        super(name);
    }

    public static SqRpcShutdownHook getRpcShutdownHook() {
        return RPC_SHUTDOWN_HOOK;
    }

    @Override
    public void run() {
        if (logger.isInfoEnabled()) {
            logger.info("Run shutdown hook now.");
        }
        doDestroy();
    }

    /**
     * Register the ShutdownHook
     */
    public void register() {
        if (!registered.get() && registered.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(getRpcShutdownHook());
        }
    }

    /**
     * Unregister the ShutdownHook
     */
    public void unregister() {
        if (registered.get() && registered.compareAndSet(true, false)) {
            Runtime.getRuntime().removeShutdownHook(getRpcShutdownHook());
        }
    }

    /**
     * Destroy all the resources, including registries and protocols.
     */
    public void doDestroy() {
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }
        // destroy all the registries
        AbstractRegistryFactory.destroyAll();
        // destroy all the protocols
        destroyProtocols();
    }

    /**
     * Destroy all the protocols.
     */
    private void destroyProtocols() {
        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        for (String protocolName : loader.getLoadedExtensions()) {
            try {
                Protocol protocol = loader.getLoadedExtension(protocolName);
                if (protocol != null) {
                    protocol.destroy();
                }
            } catch (Throwable t) {
                logger.warn(t.getMessage(), t);
            }
        }
    }


}
