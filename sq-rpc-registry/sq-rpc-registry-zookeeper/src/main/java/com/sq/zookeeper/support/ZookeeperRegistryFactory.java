package com.sq.zookeeper.support;

import com.sq.common.URL;
import com.sq.registry.Registry;
import com.sq.registry.support.AbstractRegistryFactory;
import com.sq.zookeeper.ZookeeperTransporter;

/**
 * ZookeeperRegistryFactory.
 *
 */
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    private ZookeeperTransporter zookeeperTransporter;

    /**
     * Invisible injection of zookeeper client via IOC/SPI
     * @param zookeeperTransporter
     */
    public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter) {
        this.zookeeperTransporter = zookeeperTransporter;
    }

    @Override
    public Registry createRegistry(URL url) {
        return new ZookeeperRegistry(url, zookeeperTransporter);
    }

}
