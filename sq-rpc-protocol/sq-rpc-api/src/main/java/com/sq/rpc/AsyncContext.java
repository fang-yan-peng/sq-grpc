package com.sq.rpc;

/**
 */
public interface AsyncContext {

    /**
     * write value and complete the async context.
     *
     * @param value invoke result
     */
    void write(Object value);

    /**
     * @return true if the async context is started
     */
    boolean isAsyncStarted();

    /**
     * change the context state to stop
     */
    boolean stop();

    /**
     * change the context state to start
     */
    void start();

    /**
     * Signal RpcContext switch.
     *
     * Note that you should use it in a new thread like this:
     * <code>
     * public class AsyncServiceImpl implements AsyncService {
     *     public String sayHello(String name) {
     *         final AsyncContext asyncContext = RpcContext.startAsync();
     *         new Thread(() -> {
     *
     *             // right place to use this method
     *             asyncContext.signalContextSwitch();
     *
     *             try {
     *                 Thread.sleep(500);
     *             } catch (InterruptedException e) {
     *                 e.printStackTrace();
     *             }
     *             asyncContext.write("Hello " + name + ", response from provider.");
     *         }).start();
     *         return null;
     *     }
     * }
     * </code>
     */
    void signalContextSwitch();
}
