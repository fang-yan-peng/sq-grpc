package com.sq.zookeeper;

/**
 * 2019-02-26
 */
public interface DataListener {

    void dataChanged(String path, Object value, EventType eventType);
}
