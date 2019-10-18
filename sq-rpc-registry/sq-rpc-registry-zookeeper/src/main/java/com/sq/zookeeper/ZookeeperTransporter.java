package com.sq.zookeeper;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;

@SPI("curator")
public interface ZookeeperTransporter {

    @Adaptive({Constants.CLIENT_KEY, Constants.TRANSPORTER_KEY})
    ZookeeperClient connect(URL url);

}
