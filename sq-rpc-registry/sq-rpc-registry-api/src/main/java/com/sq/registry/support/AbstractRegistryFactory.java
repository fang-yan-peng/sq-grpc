package com.sq.registry.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.URLBuilder;
import com.sq.registry.Registry;
import com.sq.registry.RegistryFactory;
import com.sq.registry.RegistryService;

/**
 * AbstractRegistryFactory. (SPI, Singleton, ThreadSafe)
 *
 * @see RegistryFactory
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    // Log output
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRegistryFactory.class);

    // The lock for the acquisition process of the registry
    private static final ReentrantLock LOCK = new ReentrantLock();

    // Registry Collection Map<RegistryAddress, Registry>
    private static final Map<String, Registry> REGISTRIES = new HashMap<>();

    /**
     * Get all registries
     *
     * @return all registries
     */
    public static Collection<Registry> getRegistries() {
        return Collections.unmodifiableCollection(REGISTRIES.values());
    }

    /**
     * Close all created registries
     */
    // TODO: 2017/8/30 to move somewhere else better
    public static void destroyAll() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Close all registries " + getRegistries());
        }
        // Lock up the registry shutdown process
        LOCK.lock();
        try {
            for (Registry registry : getRegistries()) {
                try {
                    registry.destroy();
                } catch (Throwable e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            REGISTRIES.clear();
        } finally {
            // Release the lock
            LOCK.unlock();
        }
    }

    @Override
    public Registry getRegistry(URL url) {
        url = URLBuilder.from(url)
                .setPath(RegistryService.class.getName())
                .addParameter(Constants.INTERFACE_KEY, RegistryService.class.getName())
                .removeParameters(Constants.EXPORT_KEY, Constants.REFER_KEY)
                .build();
        String key = url.toServiceStringWithoutResolving();
        // Lock the registry access process to ensure a single instance of the registry
        LOCK.lock();
        try {
            Registry registry = REGISTRIES.get(key);
            if (registry != null) {
                return registry;
            }
            //create registry by spi/ioc
            registry = createRegistry(url);
            if (registry == null) {
                throw new IllegalStateException("Can not create registry " + url);
            }
            REGISTRIES.put(key, registry);
            return registry;
        } finally {
            // Release the lock
            LOCK.unlock();
        }
    }

    protected abstract Registry createRegistry(URL url);

}
