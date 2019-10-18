package com.sq.registry;


import com.sq.common.URL;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;
import com.sq.registry.support.AbstractRegistryFactory;

/**
 * RegistryFactory. (SPI, Singleton, ThreadSafe)
 *
 * @see AbstractRegistryFactory
 */
@SPI("sq.rpc")
public interface RegistryFactory {

    /**
     * Connect to the registry
     * <p>
     * Connecting the registry needs to support the contract: <br>
     * 1. When the check=false is set, the connection is not checked, otherwise the exception is thrown when disconnection <br>
     * 2. Support username:password authority authentication on URL.<br>
     * 3. Support the backup=10.20.153.10 candidate registry cluster address.<br>
     * 4. Support file=registry.cache local disk file cache.<br>
     * 5. Support the timeout=1000 request timeout setting.<br>
     * 6. Support session=60000 session timeout or expiration settings.<br>
     *
     * @param url Registry address, is not allowed to be empty
     * @return Registry reference, never return empty value
     */
    @Adaptive({"protocol"})
    Registry getRegistry(URL url);

}