package com.sq.zookeeper.curator;

import com.sq.common.URL;
import com.sq.zookeeper.ZookeeperClient;
import com.sq.zookeeper.support.AbstractZookeeperTransporter;

public class CuratorZookeeperTransporter extends AbstractZookeeperTransporter {

    @Override
    public ZookeeperClient createZookeeperClient(URL url) {
        return new CuratorZookeeperClient(url);
    }
}
