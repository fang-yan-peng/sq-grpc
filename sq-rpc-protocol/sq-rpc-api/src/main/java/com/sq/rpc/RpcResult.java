package com.sq.rpc;

import java.lang.reflect.Field;

import com.sq.rpc.support.AbstractResult;

/**
 * RPC Result.
 *
 * @serial Don't change the class name and properties.
 */
public class RpcResult extends AbstractResult {

    private static final long serialVersionUID = -6925924956850004727L;

    public RpcResult() {
    }

    public RpcResult(Object result) {
        this.result = result;
    }

    public RpcResult(Throwable exception) {
        this.exception = handleStackTraceNull(exception);
    }

    @Override
    public Object recreate() throws Throwable {
        if (exception != null) {
            throw exception;
        }
        return result;
    }


    @Override
    public Object getValue() {
        return result;
    }

    public void setValue(Object value) {
        this.result = value;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable e) {
        this.exception = handleStackTraceNull(e);
    }

    @Override
    public boolean hasException() {
        return exception != null;
    }

    @Override
    public String toString() {
        return "RpcResult [result=" + result + ", exception=" + exception + "]";
    }

    /**
     * we need to deal the exception whose stack trace is null.
     * <p>
     *
     * @param e exception
     * @return exception after deal with stack trace
     */
    private Throwable handleStackTraceNull(Throwable e) {
        if (e != null) {
            try {
                // get Throwable class
                Class clazz = e.getClass();
                while (!clazz.getName().equals(Throwable.class.getName())) {
                    clazz = clazz.getSuperclass();
                }
                // get stackTrace value
                Field stackTraceField = clazz.getDeclaredField("stackTrace");
                stackTraceField.setAccessible(true);
                Object stackTrace = stackTraceField.get(e);
                if (stackTrace == null) {
                    e.setStackTrace(new StackTraceElement[0]);
                }
            } catch (Throwable t) {
                // ignore
            }
        }

        return e;
    }
}
