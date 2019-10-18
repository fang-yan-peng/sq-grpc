package com.sq.etcd.jetcd;


import com.sq.common.URL;
import com.sq.etcd.EtcdClient;
import com.sq.etcd.EtcdTransporter;

public class JEtcdTransporter implements EtcdTransporter {

    @Override
    public EtcdClient connect(URL url) {
        return new JEtcdClient(url);
    }

}
