package com.sq.protocol.grpc;

import java.util.Map;

import com.sq.rpc.Exporter;
import com.sq.rpc.Invoker;
import com.sq.rpc.protocol.AbstractExporter;

/**
 * GrpcExporter
 */
public class GrpcExporter<T> extends AbstractExporter<T> {

    private final String key;

    private final Map<String, Exporter<?>> exporterMap;

    public GrpcExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap) {
        super(invoker);
        this.key = key;
        this.exporterMap = exporterMap;
    }

    @Override
    public void unexport() {
        super.unexport();
        exporterMap.remove(key);
    }

}