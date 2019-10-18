package com.sq.etcd.support;

import com.sq.common.URL;
import com.sq.etcd.EtcdTransporter;
import com.sq.registry.Registry;
import com.sq.registry.support.AbstractRegistryFactory;

public class EtcdRegistryFactory extends AbstractRegistryFactory {

    private EtcdTransporter etcdTransporter;

    @Override
    protected Registry createRegistry(URL url) {
        return new EtcdRegistry(url, etcdTransporter);
    }

    public void setEtcdTransporter(EtcdTransporter etcdTransporter) {
        this.etcdTransporter = etcdTransporter;
    }
}
