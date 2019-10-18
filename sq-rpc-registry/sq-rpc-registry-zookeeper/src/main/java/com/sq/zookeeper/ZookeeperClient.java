package com.sq.zookeeper;

import java.util.List;
import java.util.concurrent.Executor;

import com.sq.common.URL;

public interface ZookeeperClient {

    void create(String path, boolean ephemeral);

    void delete(String path);

    List<String> getChildren(String path);

    List<String> addChildListener(String path, ChildListener listener);

    /**
     * @param path:    directory. All of child of path will be listened.
     * @param listener
     */
    void addDataListener(String path, DataListener listener);

    /**
     * @param path:    directory. All of child of path will be listened.
     * @param listener
     * @param executor another thread
     */
    void addDataListener(String path, DataListener listener, Executor executor);

    void removeDataListener(String path, DataListener listener);

    void removeChildListener(String path, ChildListener listener);

    void addStateListener(StateListener listener);

    void removeStateListener(StateListener listener);

    boolean isConnected();

    void close();

    URL getUrl();

    void create(String path, String content, boolean ephemeral);

    String getContent(String path);

}
