package com.sq.etcd.jetcd;

import io.etcd.jetcd.Client;

public interface ConnectionStateListener {

    /**
     * Called when there is a state change in the connection
     *
     * @param client   the client
     * @param newState the new state
     */
    void stateChanged(Client client, int newState);
}