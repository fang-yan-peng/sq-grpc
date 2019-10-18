package com.sq.protocol.grpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.AtomicPositiveInteger;
import com.sq.protocol.grpc.Interceptor.GrpcClientInterceptor;
import com.sq.rpc.Invocation;
import com.sq.rpc.Result;
import com.sq.rpc.RpcResult;
import com.sq.rpc.protocol.AbstractInvoker;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;

/**
 *
 * @author yanpengfang
 * create 2019-10-15 12:03 PM
 */
public class GrpcInvoker<T> extends AbstractInvoker<T> {

    private final ManagedChannel[] channels;

    private final AtomicPositiveInteger index = new AtomicPositiveInteger();

    /*private final String version;*/

    private final ReentrantLock destroyLock = new ReentrantLock();

    private final T[] clients;

    @SuppressWarnings("unchecked")
    public GrpcInvoker(Class<T> type, URL url, ManagedChannel[] channels) {
        super(type, url, new String[]{Constants.INTERFACE_KEY, Constants.GROUP_KEY, Constants.TOKEN_KEY, Constants.TIMEOUT_KEY});
        this.channels = channels;
        /*this.version = url.getParameter(Constants.VERSION_KEY, "0.0.0");*/
        Class<?> grpcClass = type.getDeclaringClass();
        if (grpcClass == null) {
            throw new RpcException("Clazz:" + type.getCanonicalName() + " is not a grpc class");
        }
        this.clients = (T[]) new Object[channels.length];
        String grpcClientName = type.getSimpleName();
        try {
            Method newStubMethod;
            if (grpcClientName.endsWith("BlockingClient")) {
                newStubMethod = grpcClass.getMethod("newBlockingStub", Channel.class);
            } else if (grpcClientName.endsWith("GreeterFutureClient")) {
                newStubMethod = grpcClass.getMethod("newFutureStub", Channel.class);
            } else {
                newStubMethod = grpcClass.getMethod("newStub", Channel.class);
            }
            if (!Modifier.isStatic(newStubMethod.getModifiers())) {
                throw new RpcException(grpcClass.getCanonicalName() + "'s method:" + newStubMethod.getName() + " is not static");
            }
            for (int i = 0; i < channels.length; ++i) {
                this.clients[i] = (T) newStubMethod.invoke(null, ClientInterceptors.intercept(channels[i], new GrpcClientInterceptor(url)));
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e);
        }
    }

    @Override
    protected Result doInvoke(Invocation invocation) {
        T currentClient;
        if (clients.length == 1) {
            currentClient = clients[0];
        } else {
            currentClient = clients[index.getAndIncrement() % clients.length];
        }
        Method m = invocation.getMethod();
        if (m == null) {
            throw new RpcException("grpc invoker method:" + invocation.getMethodName() + " can not be null");
        }
        try {
            return new RpcResult(m.invoke(currentClient, invocation.getArguments()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RpcException("sq rpc method " + invocation.getMethodName(), e);
        }
    }

    @Override
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        for (ManagedChannel client : channels) {
            if (!client.isTerminated()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        // in order to avoid closing a client multiple times, a counter is used in case of connection per jvm, every
        // time when client.close() is called, counter counts down once, and when counter reaches zero, client will be
        // closed.
        if (super.isDestroyed()) {
            return;
        }
        // double check to avoid dup close
        destroyLock.lock();
        try {
            if (super.isDestroyed()) {
                return;
            }
            super.destroy();
            for (ManagedChannel client : channels) {
                try {
                    client.shutdown();
                    client.awaitTermination(5, TimeUnit.SECONDS);
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }

        } finally {
            destroyLock.unlock();
        }
    }
}
