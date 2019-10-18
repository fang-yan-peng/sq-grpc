package com.sq.rpc.cluster.router;

import com.sq.common.URL;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;

public abstract class AbstractRouter implements Router {
    protected int priority = DEFAULT_PRIORITY;
    protected boolean force = false;
    protected URL url;

    protected DynamicConfiguration configuration;

    public AbstractRouter(DynamicConfiguration configuration, URL url) {
        this.configuration = configuration;
        this.url = url;
    }
    public AbstractRouter() {
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    /*public void setConfiguration(DynamicConfiguration configuration) {
        this.configuration = configuration;
    }*/

    @Override
    public boolean isRuntime() {
        return true;
    }

    @Override
    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}
