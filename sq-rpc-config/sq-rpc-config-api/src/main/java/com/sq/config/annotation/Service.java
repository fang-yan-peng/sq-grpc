package com.sq.config.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sq.common.Constants;

/**
 * Service annotation
 *
 * @export
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Service {

    /**
     * Interface class, default value is void.class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * Interface class name, default value is empty string
     */
    String interfaceName() default "";

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

    /**
     * Service path, default value is empty string
     */
    String path() default "";

    /**
     * Whether to export services, default value is true
     */
    boolean export() default true;

    /**
     * Service token, default value is false
     */
    String token() default "";

    /**
     * Whether the services is deprecated, default value is false
     */
    boolean deprecated() default false;

    /**
     * Whether the services is dynamic, default value is true
     */
    boolean dynamic() default true;

    /**
     * Access log for the services, default value is ""
     */
    String accesslog() default "";

    /**
     * Maximum concurrent executes for the services, default value is 0 - no limits
     */
    int executes() default 0;

    /**
     * Whether to register the services to register center, default value is true
     */
    boolean register() default true;

    /**
     * Service weight value, default value is 0
     */
    int weight() default 0;

    /**
     * Service doc, default value is ""
     */
    String document() default "";

    /**
     * Delay time for services registration, default value is 0
     */
    int delay() default 0;

    /**
     * @see Service#stub()
     * @deprecated
     */
    String local() default "";

    /**
     * Service stub name, use interface name + Local if not set
     */
    String stub() default "";

    /**
     * Cluster strategy, legal values include: failover, failfast, failsafe, failback, forking
     */
    String cluster() default "";

    /**
     * How the proxy is generated, legal values include: jdk, javassist
     */
    String proxy() default "";

    /**
     * Maximum connections services provider can accept, default value is 0 - connection is shared
     */
    int connections() default 0;

    /**
     * The callback instance limit peer connection
     *
     * @see Constants#DEFAULT_CALLBACK_INSTANCES
     */
    int callbacks() default Constants.DEFAULT_CALLBACK_INSTANCES;

    /**
     * Callback method name when connected, default value is empty string
     */
    String onconnect() default "";

    /**
     * Callback method name when disconnected, default value is empty string
     */
    String ondisconnect() default "";

    /**
     * Service owner, default value is empty string
     */
    String owner() default "";

    /**
     * Service layer, default value is empty string
     */
    String layer() default "";

    /**
     * Service invocation retry times
     *
     */
    int retries() default Constants.DEFAULT_RETRIES;

    /**
     * Load balance strategy, legal values include: random, roundrobin, leastactive
     **/
    String loadbalance() default Constants.DEFAULT_LOADBALANCE;

    /**
     * Whether to enable async invocation, default value is false
     */
    boolean async() default false;

    /**
     * Maximum active requests allowed, default value is 0
     */
    int actives() default 0;

    /**
     * Whether the async request has already been sent, the default value is false
     */
    boolean sent() default false;

    /**
     * Service mock name, use interface name + Mock if not set
     */
    String mock() default "";

    /**
     * Whether to use JSR303 validation, legal values are: true, false
     */
    String validation() default "";

    /**
     * Timeout value for services invocation, default value is 0
     */
    int timeout() default 0;

    /**
     * Specify cache implementation for services invocation, legal values include: lru, threadlocal, jcache
     */
    String cache() default "";

    /**
     * Filters for services invocation
     *
     * */
    String[] filter() default {};

    String interceptor() default "";

    /**
     * Listeners for services exporting and unexporting
     *
     * */
    String[] listener() default {};

    /**
     * Customized parameter key-value pair, for example: {key1, value1, key2, value2}
     */
    String[] parameters() default {};

    /**
     * Application spring bean name
     */
    String application() default "";

    /**
     * Module spring bean name
     */
    String module() default "";

    /**
     * Provider spring bean name
     */
    String provider() default "";

    /**
     * Protocol spring bean names
     */
    String[] protocol() default {};

    /**
     * Monitor spring bean name
     */
    String monitor() default "";

    /**
     * Registry spring bean name
     */
    String[] registry() default {};

    /**
     * Service tag name
     */
    String tag() default "";

    /**
     * methods support
     * @return
     */
    Method[] methods() default {};
}
