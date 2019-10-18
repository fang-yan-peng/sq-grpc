package com.sq.rpc.module;


import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represent a application which is using rpc and store basic metadata info for using
 * during the processing of RPC invoking.
 *
 * ApplicationModel includes many ProviderModel which is about published services
 * and many Consumer Model which is about subscribed services.
 *
 * adjust project structure in order to fully utilize the methods introduced here.
 */
public class ApplicationModel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ApplicationModel.class);

    /**
     * full qualified class name -> provided services
     */
    private static final ConcurrentMap<String, ProviderModel> PROVIDED_SERVICES = new ConcurrentHashMap<>();
    /**
     * full qualified class name -> subscribe services
     */
    private static final ConcurrentMap<String, ConsumerModel> CONSUMED_SERVICES = new ConcurrentHashMap<>();

    private static String application;

    public static Collection<ConsumerModel> allConsumerModels() {
        return CONSUMED_SERVICES.values();
    }

    public static Collection<ProviderModel> allProviderModels() {
        return PROVIDED_SERVICES.values();
    }

    public static ProviderModel getProviderModel(String serviceName) {
        return PROVIDED_SERVICES.get(serviceName);
    }

    public static ConsumerModel getConsumerModel(String serviceName) {
        return CONSUMED_SERVICES.get(serviceName);
    }

    public static void initConsumerModel(String serviceName, ConsumerModel consumerModel) {
        if (CONSUMED_SERVICES.putIfAbsent(serviceName, consumerModel) != null) {
            LOGGER.warn("Already register the same consumer:" + serviceName);
        }
    }

    public static void initProviderModel(String serviceName, ProviderModel providerModel) {
        if (PROVIDED_SERVICES.putIfAbsent(serviceName, providerModel) != null) {
            LOGGER.warn("Already register the same:" + serviceName);
        }
    }

    public static String getApplication() {
        return application;
    }

    public static void setApplication(String application) {
        ApplicationModel.application = application;
    }

    /**
     * For unit test
     */
    public static void reset() {
        PROVIDED_SERVICES.clear();
        CONSUMED_SERVICES.clear();
    }
}
