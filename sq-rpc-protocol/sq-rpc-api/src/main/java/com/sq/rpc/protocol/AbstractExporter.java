package com.sq.rpc.protocol;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.rpc.Exporter;
import com.sq.rpc.Invoker;

/**
 * AbstractExporter.
 */
public abstract class AbstractExporter<T> implements Exporter<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Invoker<T> invoker;

    private volatile boolean unexported = false;

    public AbstractExporter(Invoker<T> invoker) {
        if (invoker == null) {
            throw new IllegalStateException("services invoker == null");
        }
        if (invoker.getInterface() == null) {
            throw new IllegalStateException("services type == null");
        }
        if (invoker.getUrl() == null) {
            throw new IllegalStateException("services url == null");
        }
        this.invoker = invoker;
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public void unexport() {
        if (unexported) {
            return;
        }
        unexported = true;
        getInvoker().destroy();
    }

    @Override
    public String toString() {
        return getInvoker().toString();
    }

}
