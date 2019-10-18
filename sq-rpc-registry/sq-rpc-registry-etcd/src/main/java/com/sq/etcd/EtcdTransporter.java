package com.sq.etcd;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;

@SPI("jetcd")
public interface EtcdTransporter {

    @Adaptive({Constants.CLIENT_KEY, Constants.TRANSPORTER_KEY})
    EtcdClient connect(URL url);

}
