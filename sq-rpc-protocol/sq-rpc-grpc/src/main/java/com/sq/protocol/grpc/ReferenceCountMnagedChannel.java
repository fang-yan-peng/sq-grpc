package com.sq.protocol.grpc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ConnectivityState;
import io.grpc.ExperimentalApi;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;

/**
 *
 * @author yanpengfang
 * create 2019-10-15 3:49 PM
 */
public class ReferenceCountMnagedChannel extends ManagedChannel {

    private final ManagedChannel channel;

    private final AtomicInteger referenceCount = new AtomicInteger(0);

    public ReferenceCountMnagedChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    @Override
    public ManagedChannel shutdown() {
        if (referenceCount.decrementAndGet() <= 0) {
            return channel.shutdown();
        }
        return this;
    }

    @Override
    public boolean isShutdown() {
        return channel.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return channel.isTerminated();
    }

    @Override
    public ManagedChannel shutdownNow() {
        return channel.shutdownNow();
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        return channel.awaitTermination(l, timeUnit);
    }

    @Override
    public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
        return channel.newCall(methodDescriptor, callOptions);
    }

    @Override
    public String authority() {
        return channel.authority();
    }

    @Override
    public ConnectivityState getState(boolean requestConnection) {
        return channel.getState(requestConnection);
    }

    @Override
    public void notifyWhenStateChanged(ConnectivityState source, Runnable callback) {
        channel.notifyWhenStateChanged(source, callback);
    }

    @Override
    public void resetConnectBackoff() {
        channel.resetConnectBackoff();
    }

    @Override
    public void enterIdle() {
        channel.enterIdle();
    }

    public void incrementAndGetCount() {
        referenceCount.incrementAndGet();
    }
}
