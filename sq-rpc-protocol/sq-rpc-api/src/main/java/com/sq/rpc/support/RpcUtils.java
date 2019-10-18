package com.sq.rpc.support;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.utils.ReflectUtils;
import com.sq.common.utils.StringUtils;
import com.sq.rpc.Invocation;
import com.sq.rpc.RpcInvocation;


/**
 * RpcUtils
 */
public class RpcUtils {

    private static final Logger logger = LoggerFactory.getLogger(RpcUtils.class);
    private static final AtomicLong INVOKE_ID = new AtomicLong(0);

    public static Class<?> getReturnType(Invocation invocation) {
        try {
            if (invocation != null && invocation.getInvoker() != null
                    && invocation.getInvoker().getUrl() != null
                    && !invocation.getMethodName().startsWith("$")) {
                String service = invocation.getInvoker().getUrl().getServiceInterface();
                if (StringUtils.isNotEmpty(service)) {
                    Class<?> invokerInterface = invocation.getInvoker().getInterface();
                    Class<?> cls = invokerInterface != null ? ReflectUtils.forName(invokerInterface.getClassLoader(), service)
                            : ReflectUtils.forName(service);
                    Method method = cls.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    if (method.getReturnType() == void.class) {
                        return null;
                    }
                    return method.getReturnType();
                }
            }
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
        return null;
    }

    // TODO why not get return type when initialize Invocation?
    public static Type[] getReturnTypes(Invocation invocation) {
        try {
            if (invocation != null && invocation.getInvoker() != null
                    && invocation.getInvoker().getUrl() != null
                    && !invocation.getMethodName().startsWith("$")) {
                String service = invocation.getInvoker().getUrl().getServiceInterface();
                if (StringUtils.isNotEmpty(service)) {
                    Class<?> invokerInterface = invocation.getInvoker().getInterface();
                    Class<?> cls = invokerInterface != null ? ReflectUtils.forName(invokerInterface.getClassLoader(), service)
                            : ReflectUtils.forName(service);
                    Method method = cls.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    if (method.getReturnType() == void.class) {
                        return null;
                    }
                    Class<?> returnType = method.getReturnType();
                    Type genericReturnType = method.getGenericReturnType();
                    if (Future.class.isAssignableFrom(returnType)) {
                        if (genericReturnType instanceof ParameterizedType) {
                            Type actualArgType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                            if (actualArgType instanceof ParameterizedType) {
                                returnType = (Class<?>) ((ParameterizedType) actualArgType).getRawType();
                                genericReturnType = actualArgType;
                            } else {
                                returnType = (Class<?>) actualArgType;
                                genericReturnType = returnType;
                            }
                        } else {
                            returnType = null;
                            genericReturnType = null;
                        }
                    }
                    return new Type[]{returnType, genericReturnType};
                }
            }
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
        return null;
    }

    public static Long getInvocationId(Invocation inv) {
        String id = inv.getAttachment(Constants.ID_KEY);
        return id == null ? null : new Long(id);
    }

    /**
     * Idempotent operation: invocation id will be added in async operation by default
     *
     * @param url
     * @param inv
     */
    public static void attachInvocationIdIfAsync(URL url, Invocation inv) {
        if (isAttachInvocationId(url, inv) && getInvocationId(inv) == null && inv instanceof RpcInvocation) {
            ((RpcInvocation) inv).setAttachment(Constants.ID_KEY, String.valueOf(INVOKE_ID.getAndIncrement()));
        }
    }

    private static boolean isAttachInvocationId(URL url, Invocation invocation) {
        String value = url.getMethodParameter(invocation.getMethodName(), Constants.AUTO_ATTACH_INVOCATIONID_KEY);
        if (value == null) {
            // add invocationid in async operation by default
            return isAsync(url, invocation);
        } else if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getMethodName(Invocation invocation) {
        if (Constants.$INVOKE.equals(invocation.getMethodName())
                && invocation.getArguments() != null
                && invocation.getArguments().length > 0
                && invocation.getArguments()[0] instanceof String) {
            return (String) invocation.getArguments()[0];
        }
        return invocation.getMethodName();
    }

    public static Object[] getArguments(Invocation invocation) {
        if (Constants.$INVOKE.equals(invocation.getMethodName())
                && invocation.getArguments() != null
                && invocation.getArguments().length > 2
                && invocation.getArguments()[2] instanceof Object[]) {
            return (Object[]) invocation.getArguments()[2];
        }
        return invocation.getArguments();
    }

    public static Class<?>[] getParameterTypes(Invocation invocation) {
        if (Constants.$INVOKE.equals(invocation.getMethodName())
                && invocation.getArguments() != null
                && invocation.getArguments().length > 1
                && invocation.getArguments()[1] instanceof String[]) {
            String[] types = (String[]) invocation.getArguments()[1];
            if (types == null) {
                return new Class<?>[0];
            }
            Class<?>[] parameterTypes = new Class<?>[types.length];
            for (int i = 0; i < types.length; i++) {
                parameterTypes[i] = ReflectUtils.forName(types[0]);
            }
            return parameterTypes;
        }
        return invocation.getParameterTypes();
    }

    public static boolean isAsync(URL url, Invocation inv) {
        boolean isAsync;
        if (Boolean.TRUE.toString().equals(inv.getAttachment(Constants.ASYNC_KEY))) {
            isAsync = true;
        } else {
            isAsync = url.getMethodParameter(getMethodName(inv), Constants.ASYNC_KEY, false);
        }
        return isAsync;
    }

    public static boolean isReturnTypeFuture(Invocation inv) {
        return Boolean.TRUE.toString().equals(inv.getAttachment(Constants.FUTURE_RETURNTYPE_KEY));
    }

    public static boolean hasFutureReturnType(Method method) {
        return CompletableFuture.class.isAssignableFrom(method.getReturnType());
    }

    public static boolean isOneway(URL url, Invocation inv) {
        boolean isOneway;
        if (Boolean.FALSE.toString().equals(inv.getAttachment(Constants.RETURN_KEY))) {
            isOneway = true;
        } else {
            isOneway = !url.getMethodParameter(getMethodName(inv), Constants.RETURN_KEY, true);
        }
        return isOneway;
    }

    public static Map<String, String> getNecessaryAttachments(Invocation inv) {
        Map<String, String> attachments = new HashMap<>(inv.getAttachments());
        attachments.remove(Constants.ASYNC_KEY);
        attachments.remove(Constants.FUTURE_GENERATED_KEY);
        return attachments;
    }

}
