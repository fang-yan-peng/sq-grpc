package com.sq.config;


import com.sq.common.Constants;
import com.sq.config.support.Parameter;

public class MetricsConfig extends AbstractConfig {

    private static final long serialVersionUID = -9089919311611546383L;

    private String port;
    private String protocol;

    public MetricsConfig() {
    }

    @Parameter(key = Constants.METRICS_PORT)
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Parameter(key = Constants.METRICS_PROTOCOL)
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

}
