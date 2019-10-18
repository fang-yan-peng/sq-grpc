package com.sq.rpc.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.Version;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.ArrayUtils;
import com.sq.common.utils.CollectionUtils;
import com.sq.common.utils.NetUtils;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.Result;
import com.sq.rpc.RpcContext;
import com.sq.rpc.RpcInvocation;
import com.sq.rpc.RpcResult;
import com.sq.rpc.support.RpcUtils;

/**
 * AbstractInvoker.
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<T> type;

    private final URL url;

    private final Map<String, String> attachment;

    private volatile boolean available = true;

    private AtomicBoolean destroyed = new AtomicBoolean(false);

    public AbstractInvoker(Class<T> type, URL url) {
        this(type, url, (Map<String, String>) null);
    }

    public AbstractInvoker(Class<T> type, URL url, String[] keys) {
        this(type, url, convertAttachment(url, keys));
    }

    public AbstractInvoker(Class<T> type, URL url, Map<String, String> attachment) {
        if (type == null) {
            throw new IllegalArgumentException("services type == null");
        }
        if (url == null) {
            throw new IllegalArgumentException("services url == null");
        }
        this.type = type;
        this.url = url;
        this.attachment = attachment == null ? null : Collections.unmodifiableMap(attachment);
    }

    private static Map<String, String> convertAttachment(URL url, String[] keys) {
        if (ArrayUtils.isEmpty(keys)) {
            return null;
        }
        Map<String, String> attachment = new HashMap<String, String>();
        for (String key : keys) {
            String value = url.getParameter(key);
            if (value != null && value.length() > 0) {
                attachment.put(key, value);
            }
        }
        return attachment;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    protected void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public void destroy() {
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }
        setAvailable(false);
    }

    public boolean isDestroyed() {
        return destroyed.get();
    }

    @Override
    public String toString() {
        return getInterface() + " -> " + (getUrl() == null ? "" : getUrl().toString());
    }

    @Override
    public Result invoke(Invocation inv) throws RpcException {
        // if invoker is destroyed due to address refresh from registry, let's allow the current invoke to proceed
        if (destroyed.get()) {
            logger.warn("Invoker for services " + this + " on consumer " + NetUtils.getLocalHost() + " is destroyed, "
                    + ", sq_rpc version is " + Version.getVersion() + ", this invoker should not be used any longer");
        }
        RpcInvocation invocation = (RpcInvocation) inv;
        invocation.setInvoker(this);
        if (CollectionUtils.isNotEmptyMap(attachment)) {
            invocation.addAttachmentsIfAbsent(attachment);
        }
        Map<String, String> contextAttachments = RpcContext.getContext().getAttachments();
        if (CollectionUtils.isNotEmptyMap(contextAttachments)) {
            /**
             * invocation.addAttachmentsIfAbsent(context){@link RpcInvocation#addAttachmentsIfAbsent(Map)}should not be used here,
             * because the {@link RpcContext#setAttachment(String, String)} is passed in the Filter when the call is triggered
             * by the built-in retry mechanism. The attachment to update RpcContext will no longer work, which is
             * a mistake in most cases (for example, through Filter to RpcContext output traceId and spanId and other information).
             */
            invocation.addAttachments(contextAttachments);
        }
        if (getUrl().getMethodParameter(invocation.getMethodName(), Constants.ASYNC_KEY, false)) {
            invocation.setAttachment(Constants.ASYNC_KEY, Boolean.TRUE.toString());
        }
        RpcUtils.attachInvocationIdIfAsync(getUrl(), invocation);
        try {
            return doInvoke(invocation);
        } catch (InvocationTargetException e) { // biz exception
            Throwable te = e.getTargetException();
            if (te == null) {
                return new RpcResult(e);
            } else {
                if (te instanceof RpcException) {
                    ((RpcException) te).setCode(RpcException.BIZ_EXCEPTION);
                }
                return new RpcResult(te);
            }
        } catch (RpcException e) {
            if (e.isBiz()) {
                return new RpcResult(e);
            } else {
                throw e;
            }
        } catch (Throwable e) {
            return new RpcResult(e);
        }
    }

    protected abstract Result doInvoke(Invocation invocation) throws Throwable;

}
