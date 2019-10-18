package com.sq.rpc.cluster.router.condition.config.center;

/**
 * Config change event type
 */
public enum ConfigChangeType {
    /**
     * A config is created.
     */
    ADDED,

    /**
     * A config is updated.
     */
    MODIFIED,

    /**
     * A config is deleted.
     */
    DELETED
}
